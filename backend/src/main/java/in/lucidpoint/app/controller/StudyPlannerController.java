package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.StudyPlanRequest;
import in.lucidpoint.app.dto.StudyPlanResponse;
import in.lucidpoint.app.service.StudyPlannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** A premium AI productivity tool (ROADMAP.md §5 Phase 3) — hard-gated, no free tier. */
@RestController
@RequestMapping("/api/ai/study-planner")
@RequiredArgsConstructor
public class StudyPlannerController {

    private final StudyPlannerService studyPlannerService;

    @PostMapping("/plan")
    @PreAuthorize("hasAuthority('TIER_PREMIUM')")
    public StudyPlanResponse createPlan(@Valid @RequestBody StudyPlanRequest request) {
        return new StudyPlanResponse(studyPlannerService.createPlan(request));
    }
}
