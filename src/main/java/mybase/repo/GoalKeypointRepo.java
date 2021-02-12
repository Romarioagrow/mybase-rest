package mybase.repo;

import mybase.domain.jpa.GoalKeypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalKeypointRepo extends JpaRepository<GoalKeypoint, Long> {
}
