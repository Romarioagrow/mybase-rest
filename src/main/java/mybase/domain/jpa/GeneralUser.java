package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "general_usr")
public class GeneralUser {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "general_usr_id")
    private Long generalUserId;

   /* @OneToMany(cascade = CascadeType.ALL, targetEntity = GoalEntity.class, fetch = FetchType.EAGER)
    private Set<GoalEntity> goalEntities;// = new ArrayList<>();*/
}
