package mybase.domain.jpa;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "goal_key_points")
public class GoalKeyPoint {


    @Id
    private Long goalID;

    String keyPointName, keyPointDescription;

}
