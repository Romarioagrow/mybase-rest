package mybase.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
     * 4. Отправить Объект UserProfile на сервер для обработки
     *
     *
     *
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
    private InstFollowers instFollowers;// = new InstFollowers();

    @Column(length = 1000)
    private String profile_picture_url, website;

    private String biography, id, ig_id, name;
    private Integer followers_count, follows_count, media_count;

    private LocalDateTime lastDataChange;



    /*private Integer followersAmount, followingAmount;
    private String biography, fullName, pic, picFull;*/


    /*@ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> followers;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> following;*/

    /*@ManyToMany
    @JoinTable(
            name = "inst_followers",
            joinColumns = @JoinColumn(name = "user_inst_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_inst_id")
    )
    private List<InstProfile> followers;*/

    /*@ManyToMany
    @JoinTable(
            name = "inst_following",
            joinColumns = @JoinColumn(name = "user_inst_id"),
            inverseJoinColumns = @JoinColumn(name = "following_inst_id")
    )
    private List<InstProfile> following;*/

    //private Object media;

    /*@ManyToMany
    @JoinTable(
            name = "inst_followers",
            joinColumns = @JoinColumn(name = "user_inst_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_inst_id")
    )
    private List<InstProfile> followers;

    @ManyToMany
    @JoinTable(
            name = "inst_following",
            joinColumns = @JoinColumn(name = "user_inst_id"),
            inverseJoinColumns = @JoinColumn(name = "following_inst_id")
    )
    private List<InstProfile> following;*/
}
