package in.lucidpoint.app.dto;

import in.lucidpoint.app.entity.Attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceEntryRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private LocalDate date;

    @NotNull
    private AttendanceStatus status;
}
