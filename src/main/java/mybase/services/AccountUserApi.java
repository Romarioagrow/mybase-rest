package mybase.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AccountUserApi extends UserDetailsService {


    ResponseEntity<?> registerUser(Map<String, String> userCredentials);
}
