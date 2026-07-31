package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.Resource;
import in.lucidpoint.app.entity.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByStatus(ResourceStatus status);
    List<Resource> findByAuthorId(Long authorId);
    List<Resource> findByStatusAndGrade(ResourceStatus status, Integer grade);
    List<Resource> findByStatusAndSubject(ResourceStatus status, String subject);
    List<Resource> findByStatusAndGradeAndSubject(ResourceStatus status, Integer grade, String subject);
}
