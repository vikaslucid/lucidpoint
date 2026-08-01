package in.lucidpoint.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LessonQuestionRequest {
    @NotBlank
    private String prompt;

    // BEGINNER, INTERMEDIATE, or ADVANCED — defaults to BEGINNER if omitted (see LessonService).
    private String difficulty;

    private List<String> options; // optional — omit/empty for a free-response question

    @NotBlank
    private String correctAnswer;

    private String hint; // optional
    private String explanation; // optional
}
