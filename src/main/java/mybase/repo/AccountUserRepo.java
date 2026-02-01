package mybase.repo;

import mybase.domain.jpa.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountUserRepo extends JpaRepository<UserAccount, Long> {

    UserAccount findAccountUserByUsername(String username);

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
