package in.lucidpoint.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** What the client receives after a successful login/register: the JWT plus basic identity. */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String fullName;
}
