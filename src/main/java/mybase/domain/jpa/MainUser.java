package mybase.domain.jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mybase.domain.AccountUser;
import mybase.domain.GoalEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class MainUser extends AccountUser {

    @OneToMany
    private List<GoalEntity> goalEntities = new ArrayList<>();

}
