package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.cookie.CookieHashSet;
import me.postaddict.instagram.scraper.cookie.DefaultCookieJar;
import me.postaddict.instagram.scraper.interceptor.ErrorInterceptor;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import mybase.domain.SpendingItem;
import mybase.repo.SpendingRepo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class DataService {
    private final SpendingRepo spendingRepo;
    public Map<String, Object> allUserSpending(String userID) {
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
