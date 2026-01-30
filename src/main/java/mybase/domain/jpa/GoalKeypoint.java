package mybase.domain.jpa;


import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@Table(name = "goal_key_points")
@NoArgsConstructor
public class GoalKeypoint implements Serializable {

    @Id
    @Column(name = "keypoint_uuid")
    private UUID keypointUUID;

    private String keypointName, keypointDescription;

    public GoalKeypoint(String keypointName, String keypointDescription) {
        this.keypointUUID = UUID.randomUUID();
        this.keypointName = keypointName;
        this.keypointDescription = keypointDescription;
    }

}
