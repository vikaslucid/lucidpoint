package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.ResourceRequest;
import in.lucidpoint.app.entity.Resource;
import in.lucidpoint.app.entity.ResourceStatus;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.ResourceRepository;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The creator publishing workflow (ROADMAP.md §3.5): DRAFT -> PENDING_REVIEW -> PUBLISHED
 * or REJECTED. Only PUBLISHED resources are publicly visible (listPublished/getPublishedById) -
 * everything else is visible only to its author (listMine) or a reviewer (listPending).
 * Revenue-share tracking deliberately isn't here yet — the roadmap calls that out as needing
 * a real billing provider (Phase 4), not this scaffold.
 */
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public Resource create(ResourceRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));

        Resource resource = Resource.builder()
                .title(request.getTitle())
                .type(request.getType())
                .summary(request.getSummary())
                .body(request.getBody())
                .externalUrl(request.getExternalUrl())
                .author(author)
                .build(); // status defaults to DRAFT

        return resourceRepository.save(resource);
    }

    public Resource submitForReview(Long id, Long requesterId) {
        Resource resource = getById(id);
        if (!resource.getAuthor().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the author can submit this resource for review");
        }
        if (resource.getStatus() != ResourceStatus.DRAFT && resource.getStatus() != ResourceStatus.REJECTED) {
            throw new IllegalArgumentException("Only a draft or rejected resource can be submitted for review");
        }

        resource.setStatus(ResourceStatus.PENDING_REVIEW);
        resource.setReviewNote(null);
        return resourceRepository.save(resource);
    }

    public Resource approve(Long id) {
        Resource resource = requirePendingReview(id);
        resource.setStatus(ResourceStatus.PUBLISHED);
        return resourceRepository.save(resource);
    }

    public Resource reject(Long id, String reviewNote) {
        Resource resource = requirePendingReview(id);
        resource.setStatus(ResourceStatus.REJECTED);
        resource.setReviewNote(reviewNote);
        return resourceRepository.save(resource);
    }

    public List<Resource> listPublished() {
        return resourceRepository.findByStatus(ResourceStatus.PUBLISHED);
    }

    public List<Resource> listPending() {
        return resourceRepository.findByStatus(ResourceStatus.PENDING_REVIEW);
    }

    public List<Resource> listMine(Long authorId) {
        return resourceRepository.findByAuthorId(authorId);
    }

    // Deliberately returns the same "not found" as a missing id rather than a 403 for a
    // draft/pending/rejected resource — an anonymous reader shouldn't learn an unpublished
    // resource exists at all, let alone read its content.
    public Resource getPublishedById(Long id) {
        Resource resource = getById(id);
        if (resource.getStatus() != ResourceStatus.PUBLISHED) {
            throw new IllegalArgumentException("Resource not found: " + id);
        }
        return resource;
    }

    private Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));
    }

    private Resource requirePendingReview(Long id) {
        Resource resource = getById(id);
        if (resource.getStatus() != ResourceStatus.PENDING_REVIEW) {
            throw new IllegalArgumentException("Resource is not pending review");
        }
        return resource;
    }
}
