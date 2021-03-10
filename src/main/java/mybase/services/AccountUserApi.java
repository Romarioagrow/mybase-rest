package mybase.services;

import mybase.domain.jpa.UserAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AccountUserApi extends UserDetailsService {

    ResponseEntity<?> registerUser(Map<String, String> userCredentials);

    Boolean userIsAuthorised(UserAccount user);
}
