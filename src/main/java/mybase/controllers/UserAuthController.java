package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.GoogleAuthUser;
import mybase.domain.UserAccount;
import mybase.services.AccountUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserAuthController {
    private final AccountUserService userService;

    @PostMapping("/auth/registration")
    private ResponseEntity<?> registration(@RequestBody Map<String, String> userCredentials) {
        return userService.registerUser(userCredentials);
    }

    @GetMapping("/auth/check")
    public Boolean userIsAuthorised(@AuthenticationPrincipal UserAccount user) {
        log.info("UserController, auth check");

        /*TODO:
        * JWT Auth
        * AuthService
        *
        * */

        return userService.userIsAuthorised(user);
    }

    @GetMapping("/auth/userAuthorized")
    public ResponseEntity<?> userAuthorized(@AuthenticationPrincipal GoogleAuthUser user) {
        return user == null ? new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED) : new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/auth/getUser")
    public UserAccount getUser(@AuthenticationPrincipal UserAccount user) {
        log.info("@AuthenticationPrincipal UserAccount user: " + user);

        userService.userIsAuthorised(user);

        return user;
    }
}
