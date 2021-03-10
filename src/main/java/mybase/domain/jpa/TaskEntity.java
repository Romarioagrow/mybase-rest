package mybase.domain.jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "task_entities")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskID;

}
