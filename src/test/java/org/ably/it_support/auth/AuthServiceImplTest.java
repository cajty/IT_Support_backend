package org.ably.it_support.auth;

import org.ably.it_support.core.exception.UnauthorizedException;
import org.ably.it_support.core.security.JwtService;
import org.ably.it_support.user.AppUser;
import org.ably.it_support.user.Role;
import org.ably.it_support.user.UserMapper;
import org.ably.it_support.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("Test User", "Test@Example.com", "password"); // Mixed case email
        loginRequest = new LoginRequest("Test@Example.com", "password"); // Mixed case email
        appUser = new AppUser();
        appUser.setEmail("test@example.com"); // Lowercase in DB
        appUser.setPassword("encodedPassword");
        appUser.setRole(Role.EMPLOYEE);
    }

    @Test
     @DisplayName("Signup - When Email Already Exists - Should Throw UnauthorizedException")
    void signup_WhenEmailAlreadyExists_ShouldThrowUnauthorizedException() {

        when(userRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.of(appUser));

        assertThrows(UnauthorizedException.class, () -> authService.signup(registerRequest));
        verify(userRepository).findByEmail("test@example.com"); // Ensure lowercase check
    }

    @Test
    @DisplayName("Signup - When Valid - Should Save User With Lowercase Email And Encoded Password")
    void signup_WhenValid_ShouldSaveUserWithLowercaseEmailAndEncodedPassword() {
        when(userRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.empty());
        when(userMapper.toEntity(registerRequest)).thenReturn(appUser);
        when(passwordEncoder.encode(eq("password"))).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("mockedToken");

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        LoginResponse response = authService.signup(registerRequest);

        verify(userRepository).save(userCaptor.capture());
        AppUser savedUser = userCaptor.getValue();


        assertEquals("test@example.com", savedUser.getEmail());

        assertEquals("encodedPassword", savedUser.getPassword());

        assertEquals(Role.EMPLOYEE, savedUser.getRole());

        verify(jwtService).generateToken(savedUser);
        assertNotNull(response.getToken());
    }

    @Test
    @DisplayName("Authenticate - When Invalid Credentials - Should Throw UnauthorizedException")
    void authenticate_WhenInvalidCredentials_ShouldThrowUnauthorizedException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(UnauthorizedException.class, () -> authService.authenticate(loginRequest));
    }

    @Test
    @DisplayName("Authenticate - When Valid - Should Use Lowercase Email And Return Token")
    void authenticate_WhenValid_ShouldUseLowercaseEmailAndReturnToken() {

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
            ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        when(authenticationManager.authenticate(authCaptor.capture()))
                .thenReturn(new UsernamePasswordAuthenticationToken(appUser, null));
        when(jwtService.generateToken(appUser)).thenReturn("mockedToken");

        LoginResponse response = authService.authenticate(loginRequest);


        UsernamePasswordAuthenticationToken authToken = authCaptor.getValue();
        assertEquals("test@example.com", authToken.getPrincipal());
        assertEquals("password", authToken.getCredentials());

        verify(jwtService).generateToken(appUser);
        assertEquals("mockedToken", response.getToken());
    }

    @Test
    @DisplayName("Authenticate - With Null Email - Should Throw UnauthorizedException")
    void authenticate_WithNullEmail_ShouldThrowUnauthorizedException() {
        LoginRequest invalidRequest = new LoginRequest(null, "password");
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(invalidRequest));
    }

    @Test
    @DisplayName("Signup - With Empty Password - Should Throw ValidationError")
    void signup_WithEmptyPassword_ShouldThrowValidationError() {
        RegisterRequest invalidRequest = new RegisterRequest("User", "user@example.com", "");
        assertThrows(Exception.class, () -> authService.signup(invalidRequest));
    }
}