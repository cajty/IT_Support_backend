package org.ably.it_support.user;

import lombok.RequiredArgsConstructor;
import org.ably.it_support.common.exception.NotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

     private final UserRepository  userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository,
                       UserMapper userMapper,
                       @Lazy PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }



    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public void existsByEmail(String email) {
        if( userRepository.existsByEmail(email)){
              throw new RuntimeException("inveliad mail  train ander one");
        }
    }
}
