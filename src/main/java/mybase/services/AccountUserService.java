package mybase.services;

import lombok.AllArgsConstructor;
import mybase.domain.AccountUser;
import mybase.repo.AccountUserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountUserService implements AccountUserApi {
    private final AccountUserRepo accountUserRepo;

    @Override
    public AccountUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountUserRepo.findAccountUserByUsername(username);
    }
}
