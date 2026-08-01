package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A short, focused teaching unit: a plain-language concept explanation followed by a small,
 * curated set of guided practice questions (LessonQuestion). This is the "guiding medium, not
 * a wall of a hundred questions" content type — deliberately separate from Resource/PROBLEM_SET,
 * which stays around for reference material (full papers, articles, videos) rather than
 * step-by-step guided practice.
 *
 * Reuses the same DRAFT -> PENDING_REVIEW -> PUBLISHED/REJECTED workflow as Resource
 * (see LessonService) so authoring/review behaves the same way across content types.
 */
@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary; // short blurb shown in listings

    @Column(nullable = false, columnDefinition = "TEXT")
    private String concept; // the plain-language explanation shown before practice questions

    private Integer grade;
    private String subject;
    private Integer sourceYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ResourceStatus status = ResourceStatus.DRAFT;

    private String reviewNote;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    private List<LessonQuestion> questions = List.of();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
