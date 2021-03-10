package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import mybase.domain.types.GoalType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Data
@Entity
@Table(name = "goal_entities")
@NoArgsConstructor//@RequiredArgsConstructor
public class GoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long goalID;

    private String goalName;

    @Lob
    private String goalText;

    private LocalDate goalSetDate, goalFinishDate;

    private Boolean isCompleted = false;

    private LocalDateTime goalSetTime;

    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "general_usr_id")
    private GeneralUser generalUser;*/
    private Long generalUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<GoalType> goalTypes;

    @OneToMany(fetch = FetchType.EAGER)
   // @LazyCollection(LazyCollectionOption.FALSE)
    private List<GoalKeypoint> goalKeyPoint;

    @OneToMany(fetch = FetchType.EAGER)
   // @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaskEntity> taskEntityList;

}
