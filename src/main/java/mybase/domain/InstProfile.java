package mybase.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Log
@Data
@Entity
@Table(name = "inst_profile")
@NoArgsConstructor
@AllArgsConstructor
public class InstProfile implements Serializable /*extends Account*/ {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "main_user_id")
    private String mainUserID;

    private Integer followersAmount, followingAmount;

    private String biography, fullName, pic, picFull;


    @ManyToMany
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
    private List<InstProfile> following;


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
