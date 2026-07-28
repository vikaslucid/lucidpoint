package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.StudentPerformanceResponse;
import in.lucidpoint.app.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/student/{studentId}")
    public StudentPerformanceResponse studentPerformance(@PathVariable Long studentId) {
        return analyticsService.getStudentPerformance(studentId);
    }
}
