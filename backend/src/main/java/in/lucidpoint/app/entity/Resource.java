package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A piece of free, publicly-readable educational content — an article, video,
 * problem set, or course. Deliberately independent of SchoolClass/Section: this
 * is the platform-wide knowledge layer, not tied to any one school's roster.
 * See ROADMAP.md §3.2.
 */
@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    @Column(nullable = false)
    private String summary; // short blurb shown in listings

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body; // article text/markdown, or a description for video/course

    private String externalUrl; // optional: link out to a video or hosted course

    // Filterable metadata (ROADMAP.md-adjacent: added ahead of digitizing a large worksheet
    // batch, so hundreds of resources don't need re-tagging later). All optional — not every
    // resource (e.g. a general-audience article) has a single grade/subject/year.
    private Integer grade;
    private String subject;
    private Integer sourceYear; // the year the original paper/content is from, if known

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Publishing workflow (ROADMAP.md §3.5) — only PUBLISHED resources are publicly visible;
    // see ResourceService for the state machine.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ResourceStatus status = ResourceStatus.DRAFT;

    private String reviewNote; // set by a reviewer on rejection; cleared on resubmission

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ResourceType {
        ARTICLE, VIDEO, PROBLEM_SET, COURSE
    }
}
