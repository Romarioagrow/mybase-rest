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

    public void instProfile() {
        /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
                .addInterceptor(new ErrorInterceptor())
                .cookieJar(new DefaultCookieJar(new CookieHashSet()))
                .build();


        Instagram instagram = new Instagram(httpClient);
        Account account = null;
        try {
            account = instagram.getAccountByUsername("romarioagrow");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(account.getMedia().getCount());

        log.info(account.getUsername());
        log.info(account.getBiography());
        log.info(account.getFullName());
        log.info(account.getBlockedByViewer());
        log.info(account.getProfilePicUrlHd());
        log.info(account.getFollowedBy() + "f");
        log.info(account.getFollows() + "follows");

        PageObject<Media> medias = null;
        try {
            medias = instagram.getMedias("romarioagrow", 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(medias.getNodes().get(0).getDisplayUrl());

        log.info(medias.getNodes().get(0).toString());*/
    }


    public void instFollowers() throws IOException {

        /*HttpHost proxy = new HttpHost("host", "port", "http");
        Instagram4j instagram = Instagram4j.builder().username("username").password("password").proxy(proxy).build();
        instagram.setup();
        instagram.login();*/


        /*Instagram4j instagram = Instagram4j.builder().username("creep_waves").password("Route456123").build();
        instagram.setup();
        instagram.login();


        InstagramSearchUsernameResult userResult = instagram.sendRequest(new InstagramSearchUsernameRequest("romarioagrow"));
        System.out.println("ID for @github is " + userResult.getUser().getPk());
        System.out.println("Number of followers: " + userResult.getUser().getFollower_count());

        InstagramGetUserFollowersResult followers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
        List<InstagramUserSummary> users = followers.getUsers();
        for (InstagramUserSummary user : users) {
            System.out.println("follower:  " + user.getUsername());
        }

        InstagramGetUserFollowersResult following = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk()));
        List<InstagramUserSummary> usersFollowing = followers.getUsers();
        for (InstagramUserSummary user : usersFollowing) {
            System.out.println("following:  " + user.getUsername());
        }*/
    }
}
