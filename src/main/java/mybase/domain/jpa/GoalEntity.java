package mybase.domain.jpa;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import mybase.domain.types.GoalType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Data
@Entity
@ToString
@Table(name = "goal_entities")
@RequiredArgsConstructor
public class GoalEntity {

    @Id
    @GeneratedValue
    private Long goalID;

    private String goalName;

    @Lob
    private String aboutGoal;

    private LocalDate goalSetDate, goalFinishDate;

    private Boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private MainUser mainUser;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<GoalType> goalTypes;

    @OneToMany(fetch = FetchType.EAGER)
    private List<GoalKeyPoint> goalKeyPoint;

    @OneToMany
    private List<TaskEntity> taskEntityList;

}
