package org.ably.it_support.user;




public interface UserService {


    AppUser findByEmail(String email);
    void existsByEmail(String email);
}