package mybase.config.security;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log
@Configuration
@EnableWebSecurity
//@EnableOAuth2Sso
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/**")
                    .authorizeRequests()
                    .mvcMatchers("/api/user/auth/**", "/user/login", "/js/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/auth")
                    .loginProcessingUrl("/user/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
                .and()
                    .logout().permitAll()
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/")
                .and().csrf().disable();
    }


    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/login**", "/js/**", "/error**").permitAll()
                .anyRequest().authenticated()
                //.anyRequest().permitAll()
                .and().logout().logoutSuccessUrl("/").permitAll()
                .and()
                .csrf().disable();
    }*/


    /*.http
                .antMatcher("/**")
                    .authorizeRequests()
                    .antMatchers("/", "/login**", "/js/**", "/error**", "/user/registration").permitAll()
                .and()
                    .authorizeRequests()
                    .antMatchers("/api/admin**").hasAuthority("ADMIN")
                    .mvcMatchers("/api/user/**").authenticated()
                    .anyRequest().permitAll()
                .and()
                    .formLogin()
                    .loginPage("/user/login")
                    .loginProcessingUrl("/user/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
                .and()
                    .logout().permitAll()
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/")
                .and().csrf().disable();*/




    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/api/user/auth/**").permitAll()
                .anyRequest().authenticated()
                //.anyRequest().permitAll()
                .and()
                    .logout().logoutSuccessUrl("/").permitAll()
                .and()
                    .csrf().disable();
    }*/

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
