package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.ResourceRequest;
import in.lucidpoint.app.dto.ReviewDecisionRequest;
import in.lucidpoint.app.entity.Resource;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.RecommendationService;
import in.lucidpoint.app.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The free knowledge layer (ROADMAP.md §3.2) plus its publishing workflow (§3.5). Public reads
 * only ever see PUBLISHED resources — creating one starts it as a private DRAFT (see
 * ResourceService), so "free, high-quality resources" stays true without also meaning
 * "anything anyone posts goes instantly live with zero review."
 */
@RestController
@RequestMapping("/api/content/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final RecommendationService recommendationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Resource create(@Valid @RequestBody ResourceRequest request,
                            @AuthenticationPrincipal UserPrincipal principal) {
        return resourceService.create(request, principal.getId());
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Resource submit(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return resourceService.submitForReview(id, principal.getId());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Resource approve(@PathVariable Long id) {
        return resourceService.approve(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public Resource reject(@PathVariable Long id, @RequestBody(required = false) ReviewDecisionRequest body) {
        String note = body != null ? body.getReviewNote() : null;
        return resourceService.reject(id, note);
    }

    @GetMapping
    public List<Resource> listPublished(@RequestParam(required = false) Integer grade,
                                         @RequestParam(required = false) String subject) {
        return resourceService.listPublished(grade, subject);
    }

    @GetMapping("/mine")
    public List<Resource> listMine(@AuthenticationPrincipal UserPrincipal principal) {
        return resourceService.listMine(principal.getId());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Resource> listPending() {
        return resourceService.listPending();
    }

    // Personalization (ROADMAP.md §4) — reads real ResourceView engagement, recorded by getById below.
    @GetMapping("/recommended")
    public List<Resource> recommended(@AuthenticationPrincipal UserPrincipal principal) {
        return recommendationService.recommendFor(principal.getId());
    }

    @GetMapping("/{id}")
    public Resource getById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        Long viewerId = principal != null ? principal.getId() : null;
        return resourceService.getPublishedById(id, viewerId);
    }
}
