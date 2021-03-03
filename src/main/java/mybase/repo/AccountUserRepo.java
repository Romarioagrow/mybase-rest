package mybase.repo;

import mybase.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountUserRepo extends JpaRepository<UserAccount, Long> {

    UserAccount findAccountUserByUsername(String username);

}
