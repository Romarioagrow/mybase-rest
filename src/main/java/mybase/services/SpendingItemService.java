package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.UserAccount;
import mybase.domain.jpa.SpendingItem;
import mybase.repo.SpendingRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class SpendingItemService {
    private final SpendingRepo spendingRepo;

    public Map<String, Object> loadAllUserSpending(Long userID) {
        Map<String, Object> payload = new LinkedHashMap<>();

        /*Все Спендинги*/
        List<SpendingItem> items = spendingRepo.findAllByUserID(userID);
        items.sort(Comparator.comparing(SpendingItem::getDate).reversed());

        /*Map с данными по валютам*/
        Map<String, Double> totalSpend = items.stream()
                .collect(Collectors.groupingBy(SpendingItem::getCurrency, Collectors.summingDouble(SpendingItem::getAmount)));

        payload.put("items", items);
        payload.put("totalSpend", totalSpend);
        return payload;
    }

    public Map<String, Object> addNewSpendingItem(UserAccount accountUser, Map<String, String> spendingData) {
        double amount = Double.parseDouble(spendingData.get("amount"));
        String type = spendingData.get("type");
        String info = spendingData.get("info");
        String currency = spendingData.get("currency");
        LocalDate date = LocalDate.parse(spendingData.get("date"));

        Long userID;

        if (userAccountAuthenticated(accountUser)) {
            userID = accountUser.getUserAccountID();
        }
        else {
            userID = 1000L;
        }

        SpendingItem spending = new SpendingItem(userID, amount, type, info, currency);
        spending.setDate(date);
        spendingRepo.save(spending);

        return loadAllUserSpending(userID);
    }

    private boolean userAccountAuthenticated(UserAccount accountUser) {
        return false;
    }

    public Map<String, Object> deleteSpendingItem(Long userID, Integer spendingID) {
        SpendingItem deleteItem = spendingRepo.findItemById(spendingID);
        spendingRepo.delete(deleteItem);
        return loadAllUserSpending(userID);
    }
}
