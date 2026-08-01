package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.LessonAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonAttemptRepository extends JpaRepository<LessonAttempt, Long> {
    List<LessonAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);
}
