package mybase.repo;

import mybase.domain.GoogleAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleUserRepo extends JpaRepository<GoogleAuthUser, String> {
}
