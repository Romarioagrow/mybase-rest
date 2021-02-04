package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.GoogleAuthUser;
import mybase.services.AccountUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserAuthController {
    private final AccountUserService userService;

    @PostMapping("/new/registration")
    private ResponseEntity<?> registration(@RequestBody Map<String, String> userCredentials) {
        return userService.registerUser(userCredentials);
    }

    @GetMapping("/auth/check")
    public Boolean hasAuth() {
        log.info("UserController, auth check");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        return principal == null ? Boolean.TRUE : Boolean.FALSE;
    }

    @GetMapping("/userAuthorized")
    public ResponseEntity<?> userAuthorized(@AuthenticationPrincipal GoogleAuthUser user) {
        return user == null ? new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED) : new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/getUser")
    public GoogleAuthUser getUser(@AuthenticationPrincipal GoogleAuthUser user) {
        return user;
    }
}
