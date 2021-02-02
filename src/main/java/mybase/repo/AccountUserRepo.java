package mybase.repo;

import mybase.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountUserRepo extends JpaRepository<AccountUser, String> {

    AccountUser findAccountUserByUsername(String username);

}
