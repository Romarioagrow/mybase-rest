package mybase.controllers;

import lombok.AllArgsConstructor;
import mybase.domain.AccountUser;
import mybase.domain.dto.GoalDto;
import mybase.domain.dto.NewGoalDto;
import mybase.domain.jpa.MainUser;
import mybase.services.GoalsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@AllArgsConstructor
public class GoalsController {

    private final GoalsService goalsService;


    @PostMapping("/add/new")
    public GoalDto addNewGoal(@RequestBody NewGoalDto newGoalDto, @AuthenticationPrincipal MainUser mainUser) {

        return goalsService.addNewGoal(newGoalDto, mainUser);//new GoalDto();
    }


    @GetMapping("/load/all")
    public List<GoalDto> loadAllGoals(@AuthenticationPrincipal AccountUser accountUser) {

        return goalsService.loadAllGoals(accountUser);

    }


    ///api/goals/load/all


}
