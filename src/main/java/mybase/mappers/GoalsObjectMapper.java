package mybase.mappers;

import mybase.domain.jpa.GoalEntity;
import mybase.domain.dto.GoalDto;
import mybase.mappers.GoalsObjectMapperApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<GoalDto> mapGoalEntitiesToDto(List<GoalEntity> allGoalEntities) {

        List<GoalDto> goalDtoList = new ArrayList<>();

        allGoalEntities.forEach(goalEntity -> {

            GoalDto goalDto = mapGoalEntityToDto(goalEntity);
            goalDtoList.add(goalDto);

        });

        return goalDtoList;
    }
}
