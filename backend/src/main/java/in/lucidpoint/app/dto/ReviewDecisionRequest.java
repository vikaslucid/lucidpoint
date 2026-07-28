package in.lucidpoint.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDecisionRequest {
    private String reviewNote; // optional — a reviewer's reason, shown to the author on rejection
}
