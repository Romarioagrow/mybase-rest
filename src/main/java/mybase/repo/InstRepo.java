package mybase.repo;

import mybase.domain.InstProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstRepo extends JpaRepository<InstProfile, String> {



}
