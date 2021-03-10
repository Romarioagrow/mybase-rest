package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.GeneralUser;
import mybase.domain.jpa.UserAccount;
import mybase.domain.types.GoalType;
import mybase.services.GoalsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/goals")
@AllArgsConstructor
public class GoalsController {
    private final GoalsService goalsService;

    @PostMapping("/add/new")
    public GoalDto addNewGoal(@RequestBody NewGoalDto newGoalDto,
                              @AuthenticationPrincipal UserAccount userAccount
    ) throws UserPrincipalNotFoundException {
        GeneralUser generalUser = userAccount.getGeneralUser();
        log.info("generalUser: {}", generalUser);
        return goalsService.addNewUserGoal(newGoalDto, userAccount);
    }

    @GetMapping("/load/all")
    public List<GoalDto> loadAllGoals(@AuthenticationPrincipal UserAccount accountUser) {
        return goalsService.loadGoalsByUser(accountUser);
    }

    @GetMapping("/load/types")
    public Set<GoalType> loadAllGoalTypes() {
        return goalsService.loadAllGoalTypes();
    }

}
