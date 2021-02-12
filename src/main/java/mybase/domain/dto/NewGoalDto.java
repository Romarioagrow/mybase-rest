package mybase.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import mybase.domain.jpa.GoalKeypoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;


@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NewGoalDto extends BaseDto {

    /*
    * 'goal_name': this.new_goal_name,
        'selected_goal_types': this.selected_goal_types,
        'start_date': this.set_start_date,
        'finish_date': this.set_finish_date,
        'key_points': this.key_points,
        'about_goal_textarea': this.about_goal_textarea
    * */

    private String goalName, aboutGoal;
    private Set<String> goalTypes;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate, finishDate;
    private ArrayList<GoalKeypointDto> keyPoints;

}
