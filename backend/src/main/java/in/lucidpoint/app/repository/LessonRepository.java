package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.Lesson;
import in.lucidpoint.app.entity.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByStatus(ResourceStatus status);
    List<Lesson> findByAuthorId(Long authorId);
    List<Lesson> findByStatusAndGrade(ResourceStatus status, Integer grade);
    List<Lesson> findByStatusAndSubject(ResourceStatus status, String subject);
    List<Lesson> findByStatusAndGradeAndSubject(ResourceStatus status, Integer grade, String subject);
}
