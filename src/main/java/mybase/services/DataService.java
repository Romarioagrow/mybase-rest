package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.SpendingItem;
import mybase.repo.SpendingRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Log
@Service
@AllArgsConstructor
public class DataService {
    private final SpendingRepo spendingRepo;
    public Map<String, Object> allUserSpending(String userID) {
        Map<String, Object> payload = new LinkedHashMap<>();

        List<SpendingItem> items = spendingRepo.findAllByUserID(userID);
        items.sort(Comparator.comparing(SpendingItem::getDate).reversed());

        double totalSpend = items.stream().mapToDouble(SpendingItem::getAmount).sum();

        payload.put("items", items);
        payload.put("totalSpend", totalSpend);

        return payload;
    }

    public Map<String, Object> addNewSpendingItem(String userID, Map<String, String> spendingData) {
        double amount = Double.parseDouble(spendingData.get("amount"));
        String type = spendingData.get("type");
        String info = spendingData.get("info");
        String currency = spendingData.get("currency");
        LocalDate date = LocalDate.parse(spendingData.get("date"));

        SpendingItem spending = new SpendingItem(userID, amount, type, info, currency);
        spending.setDate(date);
        spendingRepo.save(spending);

        return allUserSpending(userID);
    }

    public Map<String, Object> deleteSpendingItem(String userID, Integer spendingID) {
        SpendingItem deleteItem = spendingRepo.findItemById(spendingID);
        spendingRepo.delete(deleteItem);
        return allUserSpending(userID);
    }
}
