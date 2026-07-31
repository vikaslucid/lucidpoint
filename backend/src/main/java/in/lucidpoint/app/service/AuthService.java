package in.lucidpoint.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import in.lucidpoint.app.dto.AuthResponse;
import in.lucidpoint.app.dto.LoginRequest;
import in.lucidpoint.app.dto.RegisterRequest;
import in.lucidpoint.app.entity.Role;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.exception.GoogleAuthNotConfiguredException;
import in.lucidpoint.app.repository.UserRepository;
import in.lucidpoint.app.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RestClient restClient = RestClient.create();

    @Value("${app.google.client-id:}")
    private String googleClientId;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // never store plaintext
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        // Delegates to Spring Security's AuthenticationManager, which uses
        // CustomUserDetailsService + PasswordEncoder under the hood to verify credentials.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        return buildAuthResponse(user);
    }

    /**
     * Verifies the Google ID token directly against Google's tokeninfo endpoint (no SDK
     * dependency needed for a single call). An existing account is matched by email; a
     * first-time Google sign-in creates a new LEARNER with a random, never-shown password
     * hash — that account can only ever authenticate via Google, since nobody (including the
     * user) knows a real password for it.
     */
    public AuthResponse loginWithGoogle(String idToken) {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new GoogleAuthNotConfiguredException(
                    "Google sign-in isn't configured on this server yet — set the GOOGLE_CLIENT_ID environment variable.");
        }

        JsonNode tokenInfo;
        try {
            tokenInfo = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException ex) {
            throw new BadCredentialsException("Invalid Google credential");
        }

        if (tokenInfo == null || !googleClientId.equals(tokenInfo.path("aud").asText())) {
            throw new BadCredentialsException("Invalid Google credential");
        }

        String email = tokenInfo.path("email").asText();
        String fullName = tokenInfo.path("name").asText(email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName(fullName)
                        .email(email)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .role(Role.LEARNER)
                        .enabled(true)
                        .build()));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getFullName());
    }
}
