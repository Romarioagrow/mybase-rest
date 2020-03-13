package mybase.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Log
@Data
@Entity
@Table(name = "inst_profile")
@ToString(exclude = "instFollowers")
@NoArgsConstructor
public class InstProfile implements Serializable /*extends Account*/ {

       /*
      "biography": "500 internal server error",
      "id": "17841401343956222",
      "ig_id": 1038252798,
      "followers_count": 219,
      "follows_count": 161,
      "media_count": 435,
      "name": "Roman Moltanovski",
      "profile_picture_url": "https://scontent.xx.fbcdn.net/v/t51.2885-15/70732757_2475522886106542_5052961549807779840_n.jpg?_nc_cat=107&_nc_sid=86c713&_nc_ohc=bOoAHxscDBkAX_BQOQr&_nc_ht=scontent.xx&oh=d9ce7bc436d308d62368954f7954b1b7&oe=5E9862A6",
      "username": "romarioagrow",
      "website": "http://linkedin.com/in/romarioagrow"
       */

    /*
     * СТРУКТУРА И АЛГОРИТМ INST_PROFILE
     * 1.V Авторизироваться через окно логина Facebook
     * 2.V Получить и наполнить Объект UserProfile
     * 3.V Сохранить Объект UserProfile в хранилище Store на клиенте
     * 4.V Отправить Объект UserProfile на сервер для обработки
     * */

    public InstProfile(String username) {
        this.username = username;
    }

    @Id
    private String username;

    @Column(name = "main_user_id")
    private String mainUserID;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private InstFollowers instFollowers;

    @Column(length = 1000)
    private String profile_picture_url, website;

    private String biography, id, ig_id, name;

    private Integer followers_count, follows_count, media_count;

    private LocalDateTime lastDataChange;
}
