package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.AuthResponse;
import in.lucidpoint.app.dto.LoginRequest;
import in.lucidpoint.app.dto.RegisterRequest;
import in.lucidpoint.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The only controller reachable without a JWT (see SecurityConfig: "/api/auth/**" is permitAll).
 * Everything else in the API requires a valid Bearer token obtained from here.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
