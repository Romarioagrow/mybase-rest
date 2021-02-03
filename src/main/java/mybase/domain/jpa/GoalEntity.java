package mybase.domain.jpa;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import mybase.domain.types.GoalType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "goal_entities")
@RequiredArgsConstructor
public class GoalEntity {

    @Id
    @GeneratedValue
    private Long goalID;

    private String goalText;

    private LocalDateTime goalSetTime;

    private Boolean isCompleted = false;

    @OneToMany
    private List<TaskEntity> taskEntityList;

    @ManyToOne(fetch = FetchType.EAGER)
    private MainUser mainUser;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<GoalType> goalTypes;


}
