package in.lucidpoint.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttemptRequest {
    // May be blank for an unanswered/skipped free-response question — graded as incorrect.
    private String selectedAnswer;
}
