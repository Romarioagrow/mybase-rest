package mybase.config;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.FBAuthUser;
import mybase.repo.FBAuthRepo;
import mybase.repo.GoogleUserRepo;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@Log
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login**", "/js/**", "/error**").permitAll()
                .anyRequest().authenticated()
                .and().logout().logoutSuccessUrl("/").permitAll()
                .and()
                .csrf().disable();
    }

    /*GOOGLE AUTH*/
    @Bean
    public PrincipalExtractor principalExtractor(FBAuthRepo userDetailsRepo) {
        return map -> {
            log.info(map.toString());

            String userID = map.get("id").toString();
            String name = map.get("name").toString();

            FBAuthUser user = userDetailsRepo.findById(userID).orElseGet(() ->
            {
                /*newUser.setUserID(userID);
                newUser.setName(map.get("name").toString());
                newUser.setEmail(map.get("email").toString());
                newUser.setLocale(map.get("locale").toString());
                newUser.setUser_pic(map.get("picture").toString());*/
                return new FBAuthUser(userID, name);
            });
            //user.setLastVisit(LocalDateTime.now());
            return userDetailsRepo.save(user);

            /*String userID = map.get("sub").toString();
            MainUser user = userDetailsRepo.findById(userID).orElseGet(() ->
            {
                MainUser newUser = new MainUser();
                newUser.setUserID(userID);
                newUser.setName(map.get("name").toString());
                newUser.setEmail(map.get("email").toString());
                newUser.setLocale(map.get("locale").toString());
                newUser.setUser_pic(map.get("picture").toString());
                return newUser;
            });
            user.setLastVisit(LocalDateTime.now());
            return userDetailsRepo.save(user);*/
        };
    }
}
