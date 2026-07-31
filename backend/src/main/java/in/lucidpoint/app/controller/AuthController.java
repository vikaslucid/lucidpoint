package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.AuthResponse;
import in.lucidpoint.app.dto.ForgotPasswordRequest;
import in.lucidpoint.app.dto.GoogleLoginRequest;
import in.lucidpoint.app.dto.LoginRequest;
import in.lucidpoint.app.dto.RegisterRequest;
import in.lucidpoint.app.dto.ResetPasswordRequest;
import in.lucidpoint.app.service.AuthService;
import in.lucidpoint.app.service.PasswordResetService;
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
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithGoogle(request.getIdToken()));
    }

    // Always 200 regardless of whether the email matches an account — see
    // PasswordResetService.requestReset for why (avoids email enumeration).
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
