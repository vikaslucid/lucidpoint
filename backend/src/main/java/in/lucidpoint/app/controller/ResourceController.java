package in.lucidpoint.app.controller;

import in.lucidpoint.app.dto.ResourceRequest;
import in.lucidpoint.app.entity.Resource;
import in.lucidpoint.app.security.UserPrincipal;
import in.lucidpoint.app.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The free knowledge layer (ROADMAP.md §3.2). Reads are public — no login
 * required — since that's the whole point: free, high-quality resources
 * accessible to anyone, not gated behind a school's roster.
 */
@RestController
@RequestMapping("/api/content/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Resource publish(@Valid @RequestBody ResourceRequest request,
                             @AuthenticationPrincipal UserPrincipal principal) {
        return resourceService.publish(request, principal.getId());
    }

    @GetMapping
    public List<Resource> listAll() {
        return resourceService.listAll();
    }

    @GetMapping("/{id}")
    public Resource getById(@PathVariable Long id) {
        return resourceService.getById(id);
    }
}
