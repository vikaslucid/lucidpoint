package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * A single assessment event, e.g. "Mid-Term 2026" for Grade 10 Mathematics.
 * Marks are recorded against a (Exam, Student) pair — see the Mark entity.
 */
@Entity
@Table(name = "exams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Mid-Term 2026"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private Double maxMarks;
}
