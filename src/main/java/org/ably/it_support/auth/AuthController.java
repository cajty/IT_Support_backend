package org.ably.it_support.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Auth Controller", description = "Authentication APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService authService;







    @Operation
    @PostMapping("/signup")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerUserDto) {
        if(authService.signup(registerUserDto)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
    }
    @Operation(summary = "Authenticate user")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginUserDto) {
        try {
            return ResponseEntity.ok(authService.authenticate(loginUserDto));
        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                     .status(HttpStatus.UNAUTHORIZED)
                     .body(new LoginResponse());
        }

     




    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("dfegdddd");
    }




}
