package mybase.repo;

import mybase.domain.jpa.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepo extends JpaRepository<GoalEntity, Long> {



   /* @Query("SELECT g FROM GoalEntity g WHERE g.mainUser.userID = (:userID) AND JOIN FETCH g.taskEntityList WHERE g.id = (:id)")
    List<GoalEntity> findGoalsByUserIdAndFetchEagerly(@Param("id") String id);
    */

    @Query("SELECT g FROM GoalEntity g WHERE g.generalUserId IS NOT NULL AND g.generalUserId = (:userID)")
    List<GoalEntity> findGoalsByUserIdAndFetchEagerly(@Param("userID") Long userID);

    List<GoalEntity> findGoalEntitiesByGeneralUserId(Long userID);

}
