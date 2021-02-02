package mybase.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.GoalEntity;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.MainUser;
import mybase.repo.AccountUserRepo;
import mybase.repo.GoalRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GoalsService implements GoalServiceApi {

    private final GoalRepo goalRepo;
    private final AccountUserRepo userRepo;
    private final GoalsObjectMapper goalsObjectMapper;



    @Override
    public GoalDto addNewGoal(NewGoalDto newGoalDto, MainUser mainUser) {
        String goalText;
        List<GoalEntity> updateUserGoals;
        GoalEntity newGoalEntity = new GoalEntity();

        goalText = newGoalDto.getGoalText();
        newGoalEntity.setGoalName(goalText);
        newGoalEntity.setGoalSetTime(LocalDateTime.now());

        if (hasUser(mainUser)) {
            newGoalEntity.setMainUser(mainUser);
            updateUserGoals = mainUser.getGoalEntities();
            updateUserGoals.add(newGoalEntity);
            mainUser.setGoalEntities(updateUserGoals);
            persistUser(mainUser);
        }

        persistNewGoal(newGoalEntity);

        return goalsObjectMapper.mapGoalEntityToDto(newGoalEntity);
    }

    private boolean hasUser(MainUser mainUser) {
        return mainUser != null;
    }

    private void persistUser(MainUser mainUser) {
        userRepo.save(mainUser);
    }

    private void persistNewGoal(GoalEntity newGoalEntity) {

        goalRepo.save(newGoalEntity);

    }

    @Override
    public List<GoalEntity> getAllGoals() {
        return goalRepo.findAll();
    }
}
