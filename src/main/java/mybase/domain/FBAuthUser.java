package mybase.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
