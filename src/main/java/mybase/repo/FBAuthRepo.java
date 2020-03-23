package mybase.repo;

import mybase.domain.FBAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FBAuthRepo extends JpaRepository<FBAuthUser, String> {
}
