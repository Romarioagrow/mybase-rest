package mybase.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
