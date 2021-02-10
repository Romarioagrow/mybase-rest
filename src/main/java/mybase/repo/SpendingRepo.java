package mybase.repo;

import mybase.domain.jpa.SpendingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpendingRepo extends JpaRepository<SpendingItem, Integer> {

    List<SpendingItem> findAllByUserID(String userID);

    SpendingItem findItemById(Integer spendingID);
}
