package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a grade/standard, e.g. "Grade 10". Named SchoolClass (not "Class")
 * to avoid colliding with java.lang.Class.
 */
@Entity
@Table(name = "school_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Grade 10"

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Section> sections = new HashSet<>();
}
