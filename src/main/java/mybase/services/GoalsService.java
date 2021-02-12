package mybase.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.UserAccount;
import mybase.domain.jpa.GoalEntity;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.MainUser;
import mybase.domain.types.GoalType;
import mybase.mappers.GoalsObjectMapper;
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
    public GoalDto addNewGoal(NewGoalDto newGoalDto, UserAccount userAccount/* MainUser mainUser*/) {
        //String goalText;
        //List<GoalEntity> updateUserGoals;
        GoalEntity newGoalEntity = new GoalEntity();
        newGoalEntity.setGoalName(newGoalDto.getGoalName());
        //newGoalEntity.setGoalTypes(newGoalDto.getSelectedGoalTypes());
        newGoalEntity.setGoalSetDate(newGoalDto.getFinishDate());
        newGoalEntity.setGoalFinishDate(newGoalDto.getFinishDate());
        newGoalEntity.setGoalKeyPoint(newGoalDto.getKeyPoints());
        newGoalEntity.setAboutGoal(newGoalDto.getAboutGoal());

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

               // ;


    }

    @Override
    public List<GoalType> loadAllGoalTypes() {
        return GoalType.getAllTypes();
    }
}
