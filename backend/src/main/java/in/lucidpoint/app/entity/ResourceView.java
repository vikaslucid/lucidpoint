package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * One row per (user, resource) — how many times they've viewed it, and when. This is the
 * engagement signal RecommendationService reads from (ROADMAP.md §4). Without it,
 * "personalization" would mean fabricating data instead of reflecting real behavior — the
 * same reason AnalyticsService reads real Mark/Attendance rows rather than guessing.
 */
@Entity
@Table(name = "resource_views", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "resource_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private LocalDateTime firstViewedAt;

    @Column(nullable = false)
    private LocalDateTime lastViewedAt;
}
