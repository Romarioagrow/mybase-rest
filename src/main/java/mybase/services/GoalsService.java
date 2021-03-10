package mybase.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.jpa.UserAccount;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.GoalEntity;
import mybase.domain.jpa.GoalKeypoint;
import mybase.domain.jpa.GeneralUser;
import mybase.domain.types.GoalType;
import mybase.mappers.GoalsObjectMapper;
import mybase.repo.AccountUserRepo;
import mybase.repo.GeneralUserRepo;
import mybase.repo.GoalKeypointRepo;
import mybase.repo.GoalRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class GoalsService implements GoalServiceApi {

    private final GoalRepo goalRepo;
    private final AccountUserRepo accountUserRepo;
    private final GeneralUserRepo generalUserRepo;
    private final GoalsObjectMapper goalsObjectMapper;
    private final GoalKeypointRepo keypointRepo;

    @Override
    @Transactional
    public GoalDto addNewUserGoal(NewGoalDto newGoalDto, UserAccount userAccount)
            throws UserPrincipalNotFoundException {

        /*
         *
         * TODO:
         *  1. To Factory
         *
         *
         * */

        GoalEntity newGoalEntity = new GoalEntity();
        newGoalEntity.setGoalName(newGoalDto.getGoalName());
        newGoalEntity.setGoalSetDate(newGoalDto.getFinishDate());
        newGoalEntity.setGoalFinishDate(newGoalDto.getFinishDate());
        newGoalEntity.setGoalText(newGoalDto.getGoalText());

        newGoalEntity.setGoalTypes(GoalType.getAllTypes()); /// TODO: Selected goal types

        List<GoalKeypoint> goalKeypoints = new ArrayList<>();

        newGoalDto.getKeyPoints().forEach(goalKeypointDto -> {
            GoalKeypoint newGoalKeyPoint = new GoalKeypoint(
                    goalKeypointDto.getKeypointName(),
                    goalKeypointDto.getKeypointDescription());
            goalKeypoints.add(newGoalKeyPoint);
        });
        keypointRepo.saveAll(goalKeypoints);
        newGoalEntity.setGoalKeyPoint(goalKeypoints);

        String goalText = newGoalDto.getGoalText();
        newGoalEntity.setGoalText(goalText);
        newGoalEntity.setGoalSetTime(LocalDateTime.now());

        GeneralUser generalUser = userAccount.getGeneralUser();

        if (!hasUser(generalUser)) {
            throw new UserPrincipalNotFoundException(null);
        }

        Long userId = generalUser.getGeneralUserId();
        newGoalEntity.setGeneralUserId(userId);

        persistNewGoal(newGoalEntity);
        return goalsObjectMapper.mapGoalEntityToDto(newGoalEntity);
    }

    private boolean hasUser(GeneralUser mainUser) {
        return mainUser != null;
    }

    private void persistGeneralUser(GeneralUser generalUser) {
        generalUserRepo.saveAndFlush(generalUser);
        //userRepo.save(mainUser);
    }

    private void persistNewGoal(GoalEntity newGoalEntity) {

        goalRepo.save(newGoalEntity);

        log.info("newGoalEntity created: {} ", newGoalEntity.toString());

    }

    @Override
    public List<GoalEntity> getAllGoals() {
        return goalRepo.findAll();
    }

    @Override
    @Transactional
    public List<GoalDto> loadGoalsByUser(UserAccount userAccount) {
        log.info("userAccount", userAccount);

        GeneralUser generalUser = userAccount.getGeneralUser();

        if (generalUser == null) {
            log.error("No General User!");
            throw new EntityNotFoundException();
        }

        Long userId = generalUser.getGeneralUserId();

        List<GoalEntity> goalEntities = goalRepo.findGoalEntitiesByGeneralUserId(userId);

        return goalsObjectMapper.mapGoalEntitiesToDto(goalEntities);
    }

    @Override
    public Set<GoalType> loadAllGoalTypes() {
        return GoalType.getAllTypes();
    }
}
