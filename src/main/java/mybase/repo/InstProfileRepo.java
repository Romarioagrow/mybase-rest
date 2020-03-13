package mybase.repo;

import mybase.domain.InstProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstProfileRepo extends JpaRepository<InstProfile, String> {


    InstProfile findByUsername(String username);
}
