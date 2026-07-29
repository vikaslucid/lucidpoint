package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.StudentPerformanceResponse;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.AnalyticsService;
import in.lucidpoint.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    // AnalyticsService.getStudentPerformance enforces that only staff (ADMIN/TEACHER) or the
    // student themself/their linked parent can view this student's performance data.
    @GetMapping("/student/{studentId}")
    public StudentPerformanceResponse studentPerformance(@PathVariable Long studentId,
                                                           @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getById(principal.getId());
        return analyticsService.getStudentPerformance(studentId, requester);
    }
}
