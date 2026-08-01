package in.lucidpoint.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LessonRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String summary;

    @NotBlank
    private String concept;

    private Integer grade; // optional
    private String subject; // optional
    private Integer sourceYear; // optional

    @NotEmpty
    @Valid
    private List<LessonQuestionRequest> questions;
}
