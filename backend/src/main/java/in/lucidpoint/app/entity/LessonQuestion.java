package in.lucidpoint.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * One guided practice question inside a Lesson (see Lesson). Rendered one at a time in the
 * lesson-view UI, not as a scrollable list — that's the whole point of splitting a lesson's
 * practice into discrete question rows instead of one big text blob like Resource.body.
 */
@Entity
@Table(name = "lesson_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore // breaks the Lesson<->LessonQuestion cycle for serialization; the client never needs this back-reference
    private Lesson lesson;

    @Column(nullable = false)
    private Integer position; // display order within the lesson

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private QuestionDifficulty difficulty = QuestionDifficulty.BEGINNER;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    // Optional inline SVG markup shown above the prompt — for questions where a diagram
    // (a trajectory, a coordinate grid, particle positions, a circuit) genuinely helps rather
    // than being described awkwardly in text. Authored inline (not an uploaded image) since
    // these are simple schematic drawings, not photos.
    @Column(columnDefinition = "TEXT")
    private String diagramSvg;

    // Multiple-choice options, in order. Empty for a free-response question.
    @ElementCollection
    @CollectionTable(name = "lesson_question_options", joinColumns = @JoinColumn(name = "lesson_question_id"))
    @Column(name = "option_text")
    @OrderColumn(name = "position")
    @Builder.Default
    private List<String> options = List.of();

    // Matched against the student's answer (case-insensitively, trimmed) to grade the question
    // client-side. For multiple choice this is the exact text of the correct option.
    @Column(nullable = false)
    private String correctAnswer;

    private String hint; // optional scaffold a student can reveal before answering

    @Column(columnDefinition = "TEXT")
    private String explanation; // optional, shown after answering regardless of right/wrong
}
