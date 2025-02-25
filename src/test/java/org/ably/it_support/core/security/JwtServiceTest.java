package org.ably.it_support.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.ably.it_support.user.AppUser;
import org.ably.it_support.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private AppUser testUser;

    private final String SECRET_KEY = Base64.getEncoder().encodeToString("test-secret-key-1234567890-1234567890".getBytes());
    private final long EXPIRATION = 3600000;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to set private fields
        Field secretKeyField = JwtService.class.getDeclaredField("secretKeyEncoded");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, SECRET_KEY);

        Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, EXPIRATION);

        jwtService.init(); // Manually trigger @PostConstruct

        when(testUser.getEmail()).thenReturn("test@example.com");
        when(testUser.getRole()).thenReturn(Role.EMPLOYEE);

        userDetails = User.builder()
                .username(testUser.getUsername())
                .password("password")
                .authorities(testUser.getAuthorities())
                .build();
    }

    @Test
    @DisplayName("Generate token - Should contain correct claims and expiration")
    void generateToken_ValidUser_ReturnsProperlyStructuredToken() {
        // When
        String token = jwtService.generateToken(testUser);

        // Then
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals(testUser.getUsername(), claims.getSubject());
        assertEquals(Role.EMPLOYEE.name(), claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(Date.from(Instant.now())));
    }

    @Test
    @DisplayName("Token validation - Valid token - Returns true")
    void isTokenValid_ValidToken_ReturnsTrue() {
        String validToken = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(validToken, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Token validation - Expired token - Returns false")
    void isTokenValid_ExpiredToken_ReturnsFalse() {
        // Given
        String expiredToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(testUser.getUsername())
                .setIssuedAt(Date.from(Instant.now().minusSeconds(3600)))
                .setExpiration(Date.from(Instant.now().minusSeconds(1800)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        // When
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token validation - Revoked token - Returns false")
    void isTokenValid_RevokedToken_ReturnsFalse() {
        String validToken = jwtService.generateToken(testUser);
        jwtService.revokeToken(validToken);

        boolean isValid = jwtService.isTokenValid(validToken, userDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token validation - Different username - Returns false")
    void isTokenValid_TokenWithDifferentUser_ReturnsFalse() {
        // Given
        String validToken = jwtService.generateToken(testUser);
        UserDetails otherUser = User.builder()
                .username("other@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        boolean isValid = jwtService.isTokenValid(validToken, otherUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Extract username - Valid token - Returns correct username")
    void extractUsername_ValidToken_ReturnsUsername() {
        String validToken = jwtService.generateToken(testUser);

        Optional<String> username = jwtService.extractUsername(validToken);

        assertTrue(username.isPresent());
        assertEquals(testUser.getUsername(), username.get());
    }

    @Test
    @DisplayName("Extract claim - Custom claim - Returns correct value")
    void extractClaim_CustomClaim_ReturnsCorrectValue() {
        String validToken = jwtService.generateToken(testUser);

        String role = jwtService.extractClaim(validToken, claims -> claims.get("role", String.class));

        assertEquals(Role.EMPLOYEE.name(), role);
    }

    @Test
    @DisplayName("Token revocation - After revocation - Token becomes invalid")
    void revokeToken_ValidToken_MarksTokenAsInvalid() {
        String validToken = jwtService.generateToken(testUser);

        jwtService.revokeToken(validToken);

        assertTrue(jwtService.getRevokedTokens().contains(validToken));
        assertFalse(jwtService.isTokenValid(validToken, userDetails));
    }

    @Test
    @DisplayName("Expiration check - Non-expired token - Returns false")
    void isTokenExpired_ValidToken_ReturnsFalse() {
        String validToken = jwtService.generateToken(testUser);

        boolean isExpired = jwtService.isTokenExpired(validToken);

        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Invalid signature - Should throw SignatureException")
    void extractAllClaims_InvalidSignature_ThrowsException() {
        String invalidSignatureToken = Jwts.builder()
                .setSubject(testUser.getUsername())
                .signWith(Keys.hmacShaKeyFor("different-secret-key-1234567890".getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(SignatureException.class, () ->
            jwtService.extractAllClaims(invalidSignatureToken));
    }

    @Test
    @DisplayName("Malformed token - Should throw JWT parsing exception")
    void extractAllClaims_MalformedToken_ThrowsException() {
        String malformedToken = "invalid.token.structure";

        assertThrows(ExpiredJwtException.class, () ->
            jwtService.extractAllClaims(malformedToken));
    }

    @Test
    @DisplayName("Token without expiration - Should be considered invalid")
    void isTokenValid_TokenWithoutExpiration_ReturnsFalse() {
        String noExpirationToken = Jwts.builder()
                .setSubject(testUser.getUsername())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtService.isTokenValid(noExpirationToken, userDetails);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Concurrent revocation - Multiple threads - Consistent revocation state")
    void revokeToken_ConcurrentAccess_MaintainsConsistentState() throws InterruptedException {
        int threadCount = 100;
        Set<Thread> threads = new HashSet<>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() ->
                jwtService.revokeToken("token-" + Thread.currentThread().getId()));
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        assertEquals(threadCount, jwtService.getRevokedTokens().size());
    }
}