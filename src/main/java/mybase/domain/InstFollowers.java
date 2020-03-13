package mybase.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Log
@Data
@Entity
//@NoArgsConstructor
public class InstFollowers  implements Serializable {


      /*
    *
    followed_by_viewer: true
    full_name: ""
    id: "566755028"
    is_private: false
    is_verified: false
    profile_pic_url: "https://scontent-frx5-1.cdninstagram.com/v/t51.2885-19/s150x150/74798020_507847756463707_4939324282887995392_n.jpg?_nc_ht=scontent-frx5-1.cdninstagram.com&_nc_ohc=n0dqDxq0LxwAX-aqSMc&oh=de9804ffac41154a1ac2fa7cf9cf6a11&oe=5E92BE1D"
    reel: Object { id: "566755028", expiring_at: 1584112435, has_pride_media: false, … }
    requested_by_viewer: false
    username: "dadaitsme"
* */

    public InstFollowers() {
        this.instRID = UUID.randomUUID().toString();
    }


    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String instRID;

    @OneToOne(mappedBy = "instFollowers")
    private InstProfile instProfile;


    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> followers = new LinkedHashMap<>();


    /*@ManyToMany
    @JoinTable(
            name = "inst_followers",
            joinColumns = @JoinColumn(name = "user_inst_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_inst_id")
    )
    private List<InstProfile> followers;*/






}
