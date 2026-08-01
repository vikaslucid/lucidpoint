package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.LessonRequest;
import in.lucidpoint.app.dto.ReviewDecisionRequest;
import in.lucidpoint.app.entity.Lesson;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Short, focused lessons (concept + a handful of guided practice questions) — see Lesson for
 * why this is separate from /api/content/resources. Same publishing workflow as resources.
 */
@RestController
@RequestMapping("/api/content/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Lesson create(@Valid @RequestBody LessonRequest request,
                          @AuthenticationPrincipal UserPrincipal principal) {
        return lessonService.create(request, principal.getId());
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Lesson submit(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return lessonService.submitForReview(id, principal.getId());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Lesson approve(@PathVariable Long id) {
        return lessonService.approve(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public Lesson reject(@PathVariable Long id, @RequestBody(required = false) ReviewDecisionRequest body) {
        String note = body != null ? body.getReviewNote() : null;
        return lessonService.reject(id, note);
    }

    @GetMapping
    public List<Lesson> listPublished(@RequestParam(required = false) Integer grade,
                                       @RequestParam(required = false) String subject) {
        return lessonService.listPublished(grade, subject);
    }

    @GetMapping("/mine")
    public List<Lesson> listMine(@AuthenticationPrincipal UserPrincipal principal) {
        return lessonService.listMine(principal.getId());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Lesson> listPending() {
        return lessonService.listPending();
    }

    @GetMapping("/{id}")
    public Lesson getById(@PathVariable Long id) {
        return lessonService.getPublishedById(id);
    }
}
