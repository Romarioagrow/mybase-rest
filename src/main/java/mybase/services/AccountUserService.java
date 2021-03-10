package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.jpa.GeneralUser;
import mybase.domain.jpa.UserAccount;
import mybase.domain.dto.UserAccountDto;
import mybase.domain.types.UserRole;
import mybase.mappers.UserAccountMapper;
import mybase.repo.AccountUserRepo;
import mybase.repo.GeneralUserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AccountUserService implements AccountUserApi {
    private final AccountUserRepo accountUserRepo;
    private final GeneralUserRepo generalUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper accountMapper;

    @Override
    public UserAccount loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username: " + username);
        return accountUserRepo.findAccountUserByUsername(username);
    }

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(Map<String, String> userCredentials) {

        /*
        *
        * TODO:
        * AccountUser -> GeneralUser Auto creation
        * Business-logic registration errors handler
        *
        * */


        if (userAlreadyExists(userCredentials)) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setPassword(passwordEncoder.encode(userCredentials.get("password")));
        userAccount.setUsername(userCredentials.get("username").trim());
        userAccount.setEmail(userCredentials.get("email").trim());
        userAccount.setRegistrationDate(LocalDateTime.now());
        userAccount.setRoles(Collections.singleton(UserRole.USER));
        userAccount.setIsEnabled(true);

        GeneralUser generalUser = new GeneralUser();
        generalUserRepo.save(generalUser);

        userAccount.setGeneralUser(generalUser);
        accountUserRepo.save(userAccount);

        UserAccountDto accountDto = accountMapper.mapUserAccountEntityToDto(userAccount);

        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }

    @Override
    public Boolean userIsAuthorised(UserAccount user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        log.info("user: " + user);
        log.info("authentication: " + authentication);
        log.info("principal: " + principal);

        return user == null ? Boolean.FALSE : Boolean.TRUE;
    }

    private boolean userAlreadyExists(Map<String, String> userCredentials) {

        String username = userCredentials.get("username");

        return accountUserRepo.findAccountUserByUsername(username) != null;
    }
}
