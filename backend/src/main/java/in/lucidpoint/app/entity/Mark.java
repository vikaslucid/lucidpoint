package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * One student's score for one exam. The (exam, student) pair is unique —
 * a student can't have two marks entries for the same exam.
 */
@Entity
@Table(name = "marks", uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private Double marksObtained;
}
