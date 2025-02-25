package org.ably.it_support.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.HandlerExceptionResolver;


import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Do filter - No Authorization header - Continues chain without authentication")
    void doFilterInternal_NoAuthHeader_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Do filter - Invalid Authorization header format - Continues chain")
    void doFilterInternal_InvalidAuthHeader_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Do filter - Valid JWT token - Sets authentication in context")
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        String validToken = "valid.jwt.token";
        String userEmail = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(Optional.of(userEmail));

        User userDetails = new User(userEmail, "", Collections.emptyList());
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);


        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userEmail, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    @DisplayName("Do filter - Invalid JWT token - Handles exception via resolver")
    void doFilterInternal_InvalidToken_HandlesException() throws Exception {
        String invalidToken = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtService.extractUsername(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
            eq(request),
            eq(response),
            isNull(),
            any(RuntimeException.class)
        );
    }

    @Test
    @DisplayName("Do filter - Valid token but user not found - Handles exception")
    void doFilterInternal_ValidTokenUserNotFound_HandlesException() throws Exception {
        String validToken = "valid.jwt.token";
        String userEmail = "nonexistent@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(Optional.of(userEmail));
        when(userDetailsService.loadUserByUsername(userEmail))
            .thenThrow(new UsernameNotFoundException("User not found"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
            eq(request),
            eq(response),
            isNull(),
            any(UsernameNotFoundException.class)
        );
    }

    @Test
    @DisplayName("Do filter - General exception - Delegates to exception resolver")
    void doFilterInternal_GeneralException_HandlesProperly() throws Exception {
        when(request.getHeader("Authorization")).thenThrow(new RuntimeException("Network error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
            eq(request),
            eq(response),
            isNull(),
            any(RuntimeException.class)
        );
    }
}