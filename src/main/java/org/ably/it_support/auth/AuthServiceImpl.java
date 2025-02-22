package org.ably.it_support.auth;

import lombok.RequiredArgsConstructor;
import org.ably.it_support.common.exception.UnauthorizedException;
import org.ably.it_support.common.security.JwtService;
import org.ably.it_support.user.AppUser;
import org.ably.it_support.user.Role;
import org.ably.it_support.user.UserMapper;
import org.ably.it_support.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Transactional
    @Override
    public LoginResponse signup(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail().toLowerCase()).isPresent()) {
            throw new UnauthorizedException("Email already exists");
        }

        AppUser appUser = userMapper.toEntity(request);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(Role.EMPLOYEE);

        userRepository.save(appUser);
        String jwtToken = jwtService.generateToken(appUser);

        return LoginResponse.builder().token(jwtToken).build();
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        try {
            AppUser appUser = (AppUser) authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            ).getPrincipal();

            String jwtToken = jwtService.generateToken(appUser);

            return LoginResponse.builder().token(jwtToken).build();
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", request.getEmail(), e);
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}
