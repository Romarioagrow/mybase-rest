package mybase.config;

import mybase.domain.MainUser;
import mybase.repo.UserRepo;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
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

    @Bean
    public PrincipalExtractor principalExtractor(UserRepo userDetailsRepo) {
        return map -> {
            String userID = map.get("sub").toString();
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
            return userDetailsRepo.save(user);
        };
    }

}
/*server.port=8080
security.require-ssl=true
server.ssl.key-store-type:PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=160816
server.ssl.key-alias=tomcat*/