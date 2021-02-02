package mybase.services;

import mybase.domain.GoalEntity;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.MainUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalServiceApi {

    GoalDto addNewGoal(NewGoalDto newGoalDto, MainUser mainUser);

    List<GoalEntity> getAllGoals();



}
