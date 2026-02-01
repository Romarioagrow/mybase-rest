package mybase.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mybase.domain.jpa.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal implements OAuth2User, OidcUser, UserDetails {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String avatarUrl;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    public static UserPrincipal create(UserAccount user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .id(user.getUserAccountID())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName() != null ? user.getName() : user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .authorities(authorities)
                .build();
    }

    public static UserPrincipal create(UserAccount user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        return UserPrincipal.builder()
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .password(userPrincipal.getPassword())
                .name(userPrincipal.getName())
                .avatarUrl(userPrincipal.getAvatarUrl())
                .authorities(userPrincipal.getAuthorities())
                .attributes(attributes)
                .build();
    }

    public static UserPrincipal create(UserAccount user, Map<String, Object> attributes,
                                       OidcIdToken idToken, OidcUserInfo userInfo) {
        UserPrincipal userPrincipal = create(user, attributes);
        return UserPrincipal.builder()
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .password(userPrincipal.getPassword())
                .name(userPrincipal.getName())
                .avatarUrl(userPrincipal.getAvatarUrl())
                .authorities(userPrincipal.getAuthorities())
                .attributes(attributes)
                .idToken(idToken)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
