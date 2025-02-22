package org.ably.it_support.auth;

import lombok.RequiredArgsConstructor;
import org.ably.it_support.common.security.JwtService;
import org.ably.it_support.user.AppUser;
import org.ably.it_support.user.UserMapper;
import org.ably.it_support.user.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;



    @Transactional
    public boolean signup(RegisterRequest request) {
        AppUser appUser = userMapper.toEntity(request);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        AppUser appUser1 = userRepository.save(appUser);
        if(appUser1 != null) {
            return true;
        }

        return false ;
    }

    public LoginResponse authenticate(LoginRequest request) {

        AppUser appUser = (AppUser) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ).getPrincipal();

        String jwtToken = jwtService.generateToken(appUser);

        return new LoginResponse(
                jwtToken

        );
    }






}