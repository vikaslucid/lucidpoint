package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Domain profile for a student. Deliberately separate from User: User handles
 * "can this person log in", Student handles "which class/section are they enrolled in,
 * what's their admission number". A future ERP-integration feature can extend this
 * without touching authentication at all.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String admissionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    // Optional link to a parent's User account, used for the Parent Dashboard
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id")
    private User parent;
}
