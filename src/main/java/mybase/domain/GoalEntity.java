package mybase.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import mybase.domain.jpa.MainUser;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "goal_entities")
@RequiredArgsConstructor
public class GoalEntity {

    @Id
    @GeneratedValue
    private Long goalID;

    private String goalName;

    private LocalDateTime goalSetTime;

    private Boolean isCompleted = false;

    @OneToMany
    private List<TaskEntity> taskEntityList;

    @ManyToOne(fetch = FetchType.EAGER)
    private MainUser mainUser;

}