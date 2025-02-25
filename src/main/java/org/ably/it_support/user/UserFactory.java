package org.ably.it_support.user;

import java.time.LocalDateTime;

public class UserFactory {

    public static AppUser createUser(String name, String email, String password, Role role) {
        return AppUser.builder()
            .name(name)
            .email(email)
            .password(password)
            .role(role)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
