package mybase.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "fb_usr")
@NoArgsConstructor
@AllArgsConstructor
public class FBAuthUser {
    @Id
    @Column(name = "user_id")
    private String userID;

    private String name;
}
