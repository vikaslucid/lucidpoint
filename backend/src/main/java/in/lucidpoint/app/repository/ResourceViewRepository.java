package in.lucidpoint.app.repository;

import in.lucidpoint.app.entity.ResourceView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceViewRepository extends JpaRepository<ResourceView, Long> {
    Optional<ResourceView> findByUserIdAndResourceId(Long userId, Long resourceId);
    List<ResourceView> findByUserId(Long userId);
}
