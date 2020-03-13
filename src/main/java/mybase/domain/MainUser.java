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
@Table(name = "main_usr")
@NoArgsConstructor
public class MainUser  {
    @Id
    @Column(name = "user_id")
    private String userID;

    private String name, user_pic, email, gender, locale;

    private LocalDateTime lastVisit;
}
