package mybase.services;

import mybase.domain.UserAccount;
import mybase.domain.jpa.GoalEntity;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.MainUser;
import mybase.domain.types.GoalType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalServiceApi {

    GoalDto addNewGoal(NewGoalDto newGoalDto, UserAccount userAccount/*MainUser mainUser*/);

    List<GoalEntity> getAllGoals();


    List<GoalDto> loadAllGoals(UserAccount accountUser);


    List<GoalType> loadAllGoalTypes();
}
