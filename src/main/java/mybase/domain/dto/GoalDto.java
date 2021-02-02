package mybase.domain.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class GoalDto extends BaseDto {


    private Long goalID;

    private String goalText;

    private LocalDateTime goalSetTime;

    private Boolean isCompleted;

}
