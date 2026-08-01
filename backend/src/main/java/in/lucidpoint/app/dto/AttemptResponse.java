package in.lucidpoint.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttemptResponse {
    private boolean correct;
    private int pointsAwarded;
    private int totalPoints; // the user's new running total, after this attempt
    private String correctAnswer;
    private String explanation;
}
