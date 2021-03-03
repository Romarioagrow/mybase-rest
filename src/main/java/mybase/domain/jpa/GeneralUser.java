package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
@ToString
@Table(name = "general_usr")
//@EqualsAndHashCode(callSuper = true)
public class GeneralUser /*extends UserAccount*/ {

    /*@Id
    @GeneratedValue(generator ="uuid", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid", strategy = "uuid2")*/
    @Id
    @GeneratedValue
    @Column(name = "general_usr_id")
    private Long generalUserId;

    /*@OneToOne
    private */

    @OneToMany(targetEntity = GoalEntity.class, fetch = FetchType.LAZY)
    private List<GoalEntity> goalEntities;// = new ArrayList<>();

}
