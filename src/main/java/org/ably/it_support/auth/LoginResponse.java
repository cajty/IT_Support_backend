package org.ably.it_support.auth;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;

}
