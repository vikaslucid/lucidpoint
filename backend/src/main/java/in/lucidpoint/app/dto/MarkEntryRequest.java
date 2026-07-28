package in.lucidpoint.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkEntryRequest {
    @NotNull
    private Long examId;

    @NotNull
    private Long studentId;

    @NotNull
    private Double marksObtained;
}
