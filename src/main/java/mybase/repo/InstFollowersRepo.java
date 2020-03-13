package mybase.repo;

import mybase.domain.InstFollowers;
import mybase.domain.InstProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstFollowersRepo extends JpaRepository<InstFollowers, String> {

    InstFollowers findByInstProfile(InstProfile instProfile);
}
