package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.AttendanceEntryRequest;
import in.lucidpoint.app.entity.Attendance;
import in.lucidpoint.app.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Attendance mark(@Valid @RequestBody AttendanceEntryRequest request) {
        return attendanceService.markAttendance(request);
    }

    @GetMapping("/student/{studentId}")
    public List<Attendance> history(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from != null && to != null) {
            return attendanceService.historyForStudent(studentId, from, to);
        }
        return attendanceService.allForStudent(studentId);
    }
}
