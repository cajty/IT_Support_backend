package org.ably.it_support.auth;




public interface AuthService {

    LoginResponse signup(RegisterRequest request);
    LoginResponse authenticate(LoginRequest request);
}