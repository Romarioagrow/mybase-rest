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
    private String goalText;

    private LocalDate goalSetDate, goalFinishDate;

    private Boolean isCompleted = false;

    private LocalDateTime goalSetTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private GeneralUser generalUser;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<GoalType> goalTypes;

    @OneToMany(fetch = FetchType.LAZY)
    private List<GoalKeypoint> goalKeyPoint;

    @OneToMany(fetch = FetchType.LAZY)
    private List<TaskEntity> taskEntityList;

}
