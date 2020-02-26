package mybase.repo;

import mybase.domain.MainUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<MainUser, String> {
}
