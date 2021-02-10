package mybase.repo;

import mybase.domain.jpa.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepo extends JpaRepository<GoalEntity, Long> {
}
