package in.lucidpoint.app.dto;

import in.lucidpoint.app.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * What the client sends to POST /api/auth/register.
 * Kept separate from the User entity so we never expose/accept fields
 * like "enabled" or "createdAt" directly from client input.
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role; // ADMIN / TEACHER / STUDENT / PARENT
}
