package mybase.domain.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "task_entities")
public class TaskEntity {

    @Id
    @GeneratedValue
    private Long taskID;

}
