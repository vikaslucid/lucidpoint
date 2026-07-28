package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
