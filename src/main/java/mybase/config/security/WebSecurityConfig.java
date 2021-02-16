package mybase.config.security;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.tools.LoginAuthenticationFailureHandler;
import mybase.domain.FBAuthUser;
import mybase.repo.FBAuthRepo;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log
@Configuration
@EnableWebSecurity
@AllArgsConstructor
//@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public LoginAuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginAuthenticationFailureHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/**")
                    .authorizeRequests()
                    .mvcMatchers("/api/user/auth/**", "/user/login", "/js/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin().successHandler((request, response, authentication) -> {
                        log.info("successHandler");
                        String text = "user text";
            // JsonHelper.createJsonFrom( body ).getBytes( StandardCharsets.UTF_8 )
                        response.setStatus(200);
                        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
                        response.getWriter().write(text);
                    })
                    .loginPage("/auth")
                    .loginProcessingUrl("/user/login")
                .failureHandler(authenticationFailureHandler())
                    .permitAll()
                .and()
                    .logout().permitAll()
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/")
                .and().csrf().disable();
    }

    /*GOOGLE AUTH*/
    @Bean
    public PrincipalExtractor principalExtractor(FBAuthRepo userDetailsRepo) {
        return map -> {
            log.info(map.toString());

            String userID = map.get("id").toString();
            String name = map.get("name").toString();

            FBAuthUser user = userDetailsRepo.findById(userID).orElseGet(() -> {
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
