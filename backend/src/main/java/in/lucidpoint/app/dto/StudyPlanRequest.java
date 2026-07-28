package in.lucidpoint.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudyPlanRequest {
    @NotBlank
    private String subjects; // e.g. "Algebra, Physics - mechanics, English essay writing"

    @NotNull
    @Positive
    private Integer hoursPerWeek;

    private LocalDate targetDate; // optional — an exam or deadline to pace the plan against

    private String notes; // optional — current level, known weak areas, constraints
}
