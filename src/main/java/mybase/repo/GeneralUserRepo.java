package mybase.repo;

import mybase.domain.jpa.GeneralUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralUserRepo extends JpaRepository<GeneralUser, Long> {
}
