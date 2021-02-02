package mybase.services;

import mybase.domain.jpa.GoalEntity;
import mybase.domain.dto.GoalDto;
import org.springframework.stereotype.Service;

@Service
public class GoalsObjectMapper implements GoalsObjectMapperApi {


    @Override
    public GoalDto mapGoalEntityToDto(GoalEntity goalEntity) {

        GoalDto goalDto = new GoalDto();
        goalDto.setGoalID(goalDto.getGoalID());
        goalDto.setGoalText(goalEntity.getGoalText());
        goalDto.setGoalSetTime(goalEntity.getGoalSetTime());
        goalDto.setIsCompleted(goalEntity.getIsCompleted());

        return goalDto;

    }
}
