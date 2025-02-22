package org.ably.it_support.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "email is require")
    @Email(message = "Email should be valid exmple: test@example.com")
    private String email;

    @NotBlank(message = "Password is require")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;


}