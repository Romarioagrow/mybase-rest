package mybase.services;

import mybase.domain.GoalEntity;
import mybase.domain.dto.NewGoalDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalServiceApi {

    GoalEntity createNewGoal(NewGoalDto newGoalDto);

    List<GoalEntity> getAllGoals();



}
