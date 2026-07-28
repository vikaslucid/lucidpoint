package in.lucidpoint.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HintRequest {
    @NotBlank
    private String problem;

    private String studentAttempt; // optional — what the student has tried so far, if anything
}
