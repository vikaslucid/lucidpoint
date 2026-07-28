package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findBySchoolClassId(Long schoolClassId);
}
