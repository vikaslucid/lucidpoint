package in.lucidpoint.app.service;

import in.lucidpoint.app.email.EmailClient;
import in.lucidpoint.app.entity.PasswordResetToken;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.PasswordResetTokenRepository;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_VALID_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailClient emailClient;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Always completes normally, whether the email matches a user or not, and whether the
     * email provider is even configured — the HTTP response must never distinguish "no such
     * account" from "account exists," or an attacker can enumerate registered emails by
     * trying addresses one at a time and watching which ones respond differently. A send
     * failure is logged server-side (where an operator can see it) instead of surfaced to
     * the caller.
     */
    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_VALID_MINUTES))
                    .used(false)
                    .build();
            tokenRepository.save(resetToken);

            String link = frontendUrl + "/reset-password?token=" + token;
            String html = "<p>Someone requested a password reset for your LucidPoint account.</p>"
                    + "<p><a href=\"" + link + "\">Click here to reset your password</a> "
                    + "(this link expires in " + TOKEN_VALID_MINUTES + " minutes).</p>"
                    + "<p>If you didn't request this, you can safely ignore this email.</p>";

            try {
                emailClient.send(user.getEmail(), "Reset your LucidPoint password", html);
            } catch (RuntimeException ex) {
                log.error("Failed to send password reset email to user {}", user.getId(), ex);
            }
        });
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset link"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired reset link");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
