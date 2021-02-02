package mybase.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NewGoalDto extends BaseDto {

    String goalText;


}
