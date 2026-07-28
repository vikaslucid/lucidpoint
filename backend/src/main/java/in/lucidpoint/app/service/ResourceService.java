package in.lucidpoint.app.service;

import in.lucidpoint.app.dto.ResourceRequest;
import in.lucidpoint.app.entity.Resource;
import in.lucidpoint.app.entity.User;
import in.lucidpoint.app.repository.ResourceRepository;
import in.lucidpoint.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public Resource publish(ResourceRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));

        Resource resource = Resource.builder()
                .title(request.getTitle())
                .type(request.getType())
                .summary(request.getSummary())
                .body(request.getBody())
                .externalUrl(request.getExternalUrl())
                .author(author)
                .build();

        return resourceRepository.save(resource);
    }

    public List<Resource> listAll() {
        return resourceRepository.findAll();
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));
    }
}
