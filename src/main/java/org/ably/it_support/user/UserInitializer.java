package org.ably.it_support.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<AppUser> defaultUsers = List.of(
            UserFactory.createUser("Admin1", "admin1@example.com", passwordEncoder.encode("admin123"), Role.IT_SUPPORT),
            UserFactory.createUser("Admin2", "admin2@example.com", passwordEncoder.encode("admin123"), Role.IT_SUPPORT)
        );

        for (AppUser user : defaultUsers) {
            if (!userRepository.existsByEmail(user.getEmail())) {
                userRepository.save(user);
            }
        }

        System.out.println("Default users inserted successfully!");
    }
}
