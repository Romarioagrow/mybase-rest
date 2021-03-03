package mybase.services;

import mybase.domain.jpa.UserAccount;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.GoalEntity;
import mybase.domain.types.GoalType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface GoalServiceApi {

    GoalDto addNewGoal(NewGoalDto newGoalDto, UserAccount userAccount/*MainUser mainUser*/);

    List<GoalEntity> getAllGoals();


    List<GoalDto> loadGoalsByUser(UserAccount accountUser);


    Set<GoalType> loadAllGoalTypes();
}
