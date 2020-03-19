package mybase.repo;

import mybase.domain.FBAuthUser;
import mybase.domain.GoogleAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FBAuthRepo extends JpaRepository<FBAuthUser, String> {
}
