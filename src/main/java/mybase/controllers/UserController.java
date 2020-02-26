package mybase.controllers;

import lombok.extern.java.Log;
import mybase.domain.MainUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/userAuthorized")
    private ResponseEntity<?> userAuthorized(@AuthenticationPrincipal MainUser user) {
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/getUser")
    private MainUser getUser(@AuthenticationPrincipal MainUser user) {
        return user;
    }
}
