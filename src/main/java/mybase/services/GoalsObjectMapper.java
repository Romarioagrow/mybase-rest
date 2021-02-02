package mybase.services;

import mybase.domain.GoalEntity;
import mybase.domain.dto.GoalDto;
import org.springframework.stereotype.Service;

@Service
public class GoalsObjectMapper implements GoalsObjectMapperApi {


    @Override
    public GoalDto mapGoalEntityToDto(GoalEntity goalEntity) {
        GoalDto goalDto = new GoalDto();

        {


        }

        return goalDto;
    }
}
