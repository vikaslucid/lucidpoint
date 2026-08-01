package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.LessonQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonQuestionRepository extends JpaRepository<LessonQuestion, Long> {
}
