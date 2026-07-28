package in.lucidpoint.app.service;

import in.lucidpoint.app.entity.Resource;
import in.lucidpoint.app.entity.ResourceStatus;
import in.lucidpoint.app.entity.ResourceView;
import in.lucidpoint.app.repository.ResourceRepository;
import in.lucidpoint.app.repository.ResourceViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generalizes AnalyticsService's pattern (ROADMAP.md §4): read from real engagement data,
 * don't fabricate it. A user with view history gets PUBLISHED resources of whichever type
 * they've engaged with most, excluding what they've already seen; a user with no history
 * yet gets the most recently published resources instead — a cold-start fallback, not a
 * special case bolted on afterward, since it reuses the exact same "unviewed, most recent"
 * ordering as the fallback within the personalized path below.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int MAX_RECOMMENDATIONS = 5;

    private final ResourceRepository resourceRepository;
    private final ResourceViewRepository resourceViewRepository;

    public List<Resource> recommendFor(Long userId) {
        List<ResourceView> history = resourceViewRepository.findByUserId(userId);
        List<Resource> published = resourceRepository.findByStatus(ResourceStatus.PUBLISHED);

        Set<Long> alreadyViewed = history.stream()
                .map(v -> v.getResource().getId())
                .collect(Collectors.toSet());

        List<Resource> unviewed = published.stream()
                .filter(r -> !alreadyViewed.contains(r.getId()))
                .sorted(Comparator.comparing(Resource::getCreatedAt).reversed())
                .toList();

        Resource.ResourceType preferredType = mostEngagedType(history);
        if (preferredType == null) {
            return unviewed.stream().limit(MAX_RECOMMENDATIONS).toList();
        }

        List<Resource> ofPreferredType = unviewed.stream()
                .filter(r -> r.getType() == preferredType)
                .limit(MAX_RECOMMENDATIONS)
                .toList();

        // Ran out of unseen content in their preferred type — better to suggest something
        // than nothing, so fall back to the broader unviewed set rather than returning empty.
        return ofPreferredType.isEmpty()
                ? unviewed.stream().limit(MAX_RECOMMENDATIONS).toList()
                : ofPreferredType;
    }

    private Resource.ResourceType mostEngagedType(List<ResourceView> history) {
        return history.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getResource().getType(),
                        Collectors.summingInt(ResourceView::getViewCount)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
