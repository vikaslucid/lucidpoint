package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
