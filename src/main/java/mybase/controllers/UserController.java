package mybase.controllers;

import lombok.extern.java.Log;
import mybase.domain.GoogleAuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/auth/check")
    public Boolean hasAuth() {
        log.info("UserController, hasAuth()");
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
