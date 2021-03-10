package mybase.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import mybase.domain.types.UserRole;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Data
@Entity
@Table(name = "account_usr")
//@MappedSuperclass
@NoArgsConstructor
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "usr_account_id")
    private Long userAccountID;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private Boolean isEnabled;

    private LocalDateTime registrationDate;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;


    /*
    * TODO:
    *  GeneralUser Lazy
    *
    * */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "generalUserId")
    private GeneralUser generalUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

}
