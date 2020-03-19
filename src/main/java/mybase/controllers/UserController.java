package mybase.controllers;

import lombok.extern.java.Log;
import mybase.domain.GoogleAuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/userAuthorized")
    private ResponseEntity<?> userAuthorized(@AuthenticationPrincipal GoogleAuthUser user) {
        return user == null ? new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED) : new ResponseEntity<>(user, HttpStatus.OK);
        /*if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);*/
    }

    @PostMapping("/getUser")
    private GoogleAuthUser getUser(@AuthenticationPrincipal GoogleAuthUser user) {
        return user;
    }


    /*server.port=8080
security.require-ssl=true
server.ssl.key-store-type:PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=160816
server.ssl.key-alias=tomcat*/
}
