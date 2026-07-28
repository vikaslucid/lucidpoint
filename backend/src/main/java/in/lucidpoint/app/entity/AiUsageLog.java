package in.lucidpoint.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * One row per (user, day) — how many AI requests they've made today. This is what makes
 * "free users get N free hints/day" (ROADMAP.md §5 Phase 2 step 4) real enforcement rather
 * than a policy statement. Same one-row-per-day-per-user shape as Attendance, for the same
 * reason: it's the natural unit for a daily cap.
 */
@Entity
@Table(name = "ai_usage_log", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int count;
}
