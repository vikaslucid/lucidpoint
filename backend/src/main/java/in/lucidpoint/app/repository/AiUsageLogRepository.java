package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
    Optional<AiUsageLog> findByUserIdAndDate(Long userId, LocalDate date);
}
