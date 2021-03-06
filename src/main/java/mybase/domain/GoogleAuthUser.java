package mybase.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "google_usr")
@NoArgsConstructor
public class GoogleAuthUser {
    @Id
    @Column(name = "user_id")
    private Long userID;

    private String name, user_pic, email, gender, locale;

    private LocalDateTime lastVisit;
}
