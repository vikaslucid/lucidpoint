package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.HintRequest;
import in.lucidpoint.app.dto.HintResponse;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.ProblemSolvingService;
import in.lucidpoint.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * The flagship AI feature (ROADMAP.md §3.3). Open to any authenticated user — not gated
 * behind TIER_PREMIUM like UserController's demo endpoint. FREE users get a limited number
 * of free hints/day (enforced in ProblemSolvingService); this is the first place free vs.
 * premium becomes real to a user, not just to the codebase.
 */
@RestController
@RequestMapping("/api/ai/problem-solving")
@RequiredArgsConstructor
public class ProblemSolvingController {

    private final ProblemSolvingService problemSolvingService;
    private final UserService userService;

    @PostMapping("/hint")
    public HintResponse getHint(@Valid @RequestBody HintRequest request,
                                 @AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getById(principal.getId());
        String hint = problemSolvingService.getHint(user, request.getProblem(), request.getStudentAttempt());
        return new HintResponse(hint);
    }
}
