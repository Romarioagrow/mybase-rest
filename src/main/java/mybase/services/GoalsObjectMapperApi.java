package mybase.services;

import mybase.domain.jpa.GoalEntity;
import mybase.domain.dto.GoalDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalsObjectMapperApi {

    GoalDto mapGoalEntityToDto(GoalEntity goalEntity);

    List<GoalDto> mapGoalEntitiesToDto(List<GoalEntity> all);
}
