package in.lucidpoint.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

/**
 * A subdivision of a SchoolClass, e.g. Section "A" of Grade 10.
 * Students belong to a Section, not directly to a SchoolClass, since that's
 * how real schools organise attendance and rosters.
 */
@Entity
@Table(name = "sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "A"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    @JsonIgnoreProperties("sections") // schoolClass.sections would otherwise re-include this section, recursing forever
    private SchoolClass schoolClass;
}
