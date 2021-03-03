package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
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

    @OneToMany
    private List<GoalEntity> goalEntities = new ArrayList<>();

}
