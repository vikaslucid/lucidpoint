package in.lucidpoint.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Every human who can log in (admin, teacher, student, parent) has exactly one User row.
 * Student and Teacher entities each hold a one-to-one link back to a User for login credentials,
 * keeping "who can log in" separate from "domain-specific profile data".
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    // Stored as a BCrypt hash — never plaintext. See SecurityConfig's PasswordEncoder bean.
    // @JsonIgnore: entities are returned directly from controllers, so without this the
    // hash would be serialized into any response that nests a User (e.g. POST /api/students).
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
