package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.UserAccount;
import mybase.domain.dto.UserAccountDto;
import mybase.domain.types.UserRole;
import mybase.mappers.UserAccountMapper;
import mybase.repo.AccountUserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AccountUserService implements AccountUserApi {
    private final AccountUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper accountMapper;

    @Override
    public UserAccount loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username: " + username);
        return userRepo.findAccountUserByUsername(username);
    }

    @Override
    public ResponseEntity<?> registerUser(Map<String, String> userCredentials) {

        if (userAlreadyExists(userCredentials)) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }

        UserAccount user = new UserAccount();
        user.setPassword(passwordEncoder.encode(userCredentials.get("password")));
        user.setUsername(userCredentials.get("username").trim());
        user.setEmail(userCredentials.get("email").trim());
        user.setRegistrationDate(LocalDateTime.now());
        user.setRoles(Collections.singleton(UserRole.USER));
        user.setIsEnabled(true);

        /*user.setFirstName(userDetails.get("firstName").trim());
        user.setLastName(userDetails.get("lastName").trim());
        user.setPatronymic(userDetails.get("patronymic").trim());*/

        userRepo.save(user);

        UserAccountDto accountDto = accountMapper.mapUserAccountEntityToDto(user);

        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }

    @Override
    public Boolean userIsAuthorised(UserAccount user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        log.info("user: " + user);
        log.info("authentication: " + authentication);
        log.info("principal: " + principal);

        //return principal == null ? Boolean.TRUE : Boolean.FALSE;
        return user == null ? Boolean.FALSE : Boolean.TRUE;
    }

    private boolean userAlreadyExists(Map<String, String> userCredentials) {

        String username = userCredentials.get("username");

        return userRepo.findAccountUserByUsername(username) != null;
    }
}
