package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@Table(name = "general_usr")
//@ToString(exclude = "goalEntities")
//@EqualsAndHashCode(callSuper = true)
public class GeneralUser /*extends UserAccount*/ {

    /*@Id
    @GeneratedValue(generator ="uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "general_usr_id")
    private Long generalUserId;

    /*@OneToOne
    private */

   /* @OneToMany(cascade = CascadeType.ALL, targetEntity = GoalEntity.class, fetch = FetchType.EAGER)
    private Set<GoalEntity> goalEntities;// = new ArrayList<>();*/

    /*public void addNewGoal(GoalEntity newGoalEntity) {



    }*/
}
