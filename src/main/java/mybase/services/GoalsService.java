package mybase.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.UserAccount;
import mybase.domain.jpa.GoalEntity;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.GoalKeypoint;
import mybase.domain.jpa.MainUser;
import mybase.domain.types.GoalType;
import mybase.mappers.GoalsObjectMapper;
import mybase.repo.AccountUserRepo;
import mybase.repo.GoalKeypointRepo;
import mybase.repo.GoalRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GoalsService implements GoalServiceApi {
    private final GoalRepo goalRepo;
    private final AccountUserRepo userRepo;
    private final GoalsObjectMapper goalsObjectMapper;
    private final GoalKeypointRepo keypointRepo;

    @Override
    public GoalDto addNewGoal(NewGoalDto newGoalDto, UserAccount userAccount/* MainUser mainUser*/) {
        GoalEntity newGoalEntity = new GoalEntity();
        newGoalEntity.setGoalName(newGoalDto.getGoalName());
        newGoalEntity.setGoalSetDate(newGoalDto.getFinishDate());
        newGoalEntity.setGoalFinishDate(newGoalDto.getFinishDate());
        newGoalEntity.setAboutGoal(newGoalDto.getAboutGoal());

        //newGoalEntity.setGoalTypes(newGoalDto.getSelectedGoalTypes());

        List<GoalKeypoint> goalKeypoints = new ArrayList<>();

        newGoalDto.getKeyPoints().forEach(goalKeypointDto -> {
            GoalKeypoint newGoalKeyPoint = new GoalKeypoint(
                    goalKeypointDto.getKeypointName(),
                    goalKeypointDto.getKeypointDescription());
            goalKeypoints.add(newGoalKeyPoint);
        });
        keypointRepo.saveAll(goalKeypoints);
        newGoalEntity.setGoalKeyPoint(goalKeypoints);


        log.info("newGoalEntity: " + newGoalEntity.toString());

        /*goalText = newGoalDto.getGoalText();
        newGoalEntity.setGoalText(goalText);
        newGoalEntity.setGoalSetTime(LocalDateTime.now());

        if (hasUser(mainUser)) {
            newGoalEntity.setMainUser(mainUser);
            updateUserGoals = mainUser.getGoalEntities();
            updateUserGoals.add(newGoalEntity);
            mainUser.setGoalEntities(updateUserGoals);
            persistUser(mainUser);
        }*/

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

    @Override
    public List<GoalDto> loadAllGoals(UserAccount accountUser) {
        log.info("accountUser", accountUser);
        return goalsObjectMapper.mapGoalEntitiesToDto(goalRepo.findAll());
    }

    @Override
    public List<GoalType> loadAllGoalTypes() {
        return GoalType.getAllTypes();
    }
}
