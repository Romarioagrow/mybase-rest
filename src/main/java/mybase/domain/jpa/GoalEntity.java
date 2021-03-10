package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import mybase.domain.types.GoalType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "goal_entities")
@NoArgsConstructor
public class GoalEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long goalID;

    private String goalName;

    @Lob
    private String goalText;

    private LocalDate goalSetDate, goalFinishDate;

    private Boolean isCompleted = false;

    private LocalDateTime goalSetTime;

    private Long generalUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<GoalType> goalTypes;

    @OneToMany(fetch = FetchType.EAGER)
    private List<GoalKeypoint> goalKeyPoint;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<TaskEntity> taskEntityList;

    /*@Override
    public int hashCode() {
        return Objects.hash(goalID, goalName, generalUser);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GoalEntity))
            return false;
        GoalEntity other = (GoalEntity) o;
        boolean goalIdEquals = (this.goalID == null && other.goalID == null)
                || (this.goalID != null && this.goalID.equals(other.goalID));
        return this.generalUser == other.generalUser && goalIdEquals;
    }*/
}
