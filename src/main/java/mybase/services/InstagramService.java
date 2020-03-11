package mybase.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import mybase.config.WebMvcConfig;
import mybase.domain.InstProfile;
import mybase.repo.InstRepo;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log
@Service
@AllArgsConstructor
public class InstagramService {
    private final InstRepo instRepo;
    private final WebMvcConfig httpClient;


    /*
     * Отедльно вывод инфо
     * Отдельно вывод постов
     * Отдельно вывод подпсичиков
     * Отдельно сборка профиля в базу
     *
     *
     * */

    public String makeURIEncodedNoAfter(String followersHash, String userID, String endCursor) {
        String query = "https://www.instagram.com/graphql/query/?query_hash=" + followersHash + "&variables=";
        String queryToEncode = "{\"id\":" + userID + ",\"include_reel\":true,\"fetch_mutual\":true,\"first\":24}";
        String encodeQuery = URLEncoder.encode(queryToEncode, Charset.defaultCharset());
        return query + encodeQuery;
    }

    public String makeURIEncoded(String followersHash, String userID, String endCursor) {
        String query = "https://www.instagram.com/graphql/query/?query_hash=" + followersHash + "&variables=";
        String queryToEncode = "{\"id\":" + userID + ",\"include_reel\":true,\"fetch_mutual\":true,\"first\":24,\"after\":\"" + endCursor + "\"}";
        String encodeQuery = URLEncoder.encode(queryToEncode, Charset.defaultCharset());
        return query + encodeQuery;
    }

    private Response buildHttpClient(OkHttpClient httpClient, Request followersRequest, int timeout) throws IOException {
        return httpClient.newBuilder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build()
                .newCall(followersRequest)
                .execute();
    }

    public LinkedList<Object> httpClientRequester(Map<String, String> dataToServer) {
        System.out.println();
        log.info("RestTemplate");
        //https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A24%7D

        final String followersHash = "c76146de99bb02f6415203be841dd25a";
        final String followingHash = "1038252798%3AwoM4qyrNWkOxkq%3A11";
        final String followersPath = "edge_followed_by";
        final String followingPath = "edge_follow";

        int totalFollowers = 0, timeout = 3;
        String followersURLInitial = "", userID = "", endCursor1, endCursor2;// = dataToServer.get("followersURL");
        String username = dataToServer.get("username");
        String sessionID = "sessionid=1038252798%3AAbjYDoDJfK6hwQ%3A13;"; ///dataToServer.get("sessionid");

        LinkedList<String> followersListRAW = new LinkedList<>();

        /*BUILD HTTP_CLIENT WITH INSTAGRAM LOGIN COOKIES: SESSION_ID*/
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", sessionID)
                                .build();
                        return chain.proceed(authorized);
                    }
                })
                .build();

        /*USER PAGE REQUEST AND GET USER_ID AND AFTER_QUERY */
        String userPageInfoUrl = "https://www.instagram.com/" + username + "/?__a=1";
        Request userPageInfoRequest = new Request.Builder().get().url(userPageInfoUrl).build();
        try
        {
            Response response = buildHttpClient(httpClient, userPageInfoRequest, timeout);
            if (response.isSuccessful())
            {
                log.info("userPageInfoResponse: " + response.toString());

                String responseString = response.body().string();
                JsonElement jsonBody = new JsonParser().parse(responseString);
                JsonObject userObject = jsonBody.getAsJsonObject().getAsJsonObject("graphql").getAsJsonObject("user");

                totalFollowers = userObject.getAsJsonObject().getAsJsonObject("edge_followed_by").get("count").getAsInt();
                userID = userObject.get("id").getAsString();
                endCursor1 = userObject.getAsJsonObject().getAsJsonObject("edge_owner_to_timeline_media").getAsJsonObject("page_info").get("end_cursor").getAsString();
                endCursor2 = userObject.getAsJsonObject().getAsJsonObject("edge_saved_media").getAsJsonObject("page_info").get("end_cursor").getAsString();

                log.info("userObject: " + userObject.toString());
                log.info("totalFollowers: " + totalFollowers);
                log.info("userID: " + userID);
                log.info("endCursor1: " + endCursor1);
                log.info("endCursor2: " + endCursor2);

                //followersURLInitial = makeURIEncoded(followersHash, userID, endCursor2);
                //log.info("followersURLInitial: " + followersURLInitial);
            }
            else {
                log.info("Error in userPage request");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ///graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables={"id":"1038252798","include_reel":true,"fetch_mutual":true,"first":24}
        //String queryToEncodeNew = "{\"id\":" + userID + ",\"include_reel\":true,\"fetch_mutual\":true,\"first\":24}";

        ///ПЕРВЫЙ ЗАПРОС БЕЗ AFTER!!!
        followersURLInitial = "https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A24%7D";
        log.info("followersURLInitial: " + followersURLInitial);

        /*FOLLOWERS QUERY REQUEST*///FIRST QUERY
        Request followersRequest = new Request.Builder().get().url(followersURLInitial).build();
        try
        {
            Response response = buildHttpClient(httpClient, followersRequest, timeout);
            if (response.isSuccessful())
            {
                log.info("response: " + response.toString());

                boolean hasNext = true;
                String responseString, endCursor;
                JsonArray edges;
                JsonElement jsonElement;
                JsonObject jsonObject, levelData, levelUser, edge_followed_by, levelInfo;

                jsonElement =  new JsonParser().parse(response.body().string());
                edge_followed_by = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject("edge_followed_by");
                levelInfo = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject("edge_followed_by").getAsJsonObject("page_info");

                log.info(edge_followed_by.toString());
                log.info(levelInfo.toString());

                hasNext = levelInfo.get("has_next_page").getAsBoolean();
                endCursor = levelInfo.get("end_cursor").getAsString();
                edges = edge_followed_by.getAsJsonArray("edges");

                followersListRAW.add(edges.toString());

                log.info("hasNext: " + hasNext);
                log.info("endCursor: " + endCursor);
                log.info("edges: " + edges);
                log.info("followersListRAW: " + followersListRAW.toString());

                while (hasNext) {

                    String followersURLNext = makeURIEncoded(followersHash, userID, endCursor);
                    System.out.println();
                    log.info("NEW followersURLNext: " + followersURLNext);

                    followersRequest = new Request.Builder().get().url(followersURLNext).build();
                    Response responseNext = buildHttpClient(httpClient, followersRequest, timeout);
                    if (responseNext.isSuccessful())
                    {
                        try
                        {
                            /*DATA FROM NEW REQUEST*/
                            jsonElement =  new JsonParser().parse(responseNext.body().string());
                            edge_followed_by = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject("edge_followed_by");
                            levelInfo = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject("edge_followed_by").getAsJsonObject("page_info");

                            hasNext = levelInfo.get("has_next_page").getAsBoolean();

                            if (hasNext) {
                                endCursor = levelInfo.get("end_cursor").getAsString();
                                log.info("getNnd_cursorNew: " + levelInfo.get("end_cursor").getAsString());
                                log.info("endCursorNew: " + endCursor);
                            }

                            edges = edge_followed_by.getAsJsonArray("edges");
                            boolean noEdges = edges.toString().equals("[]");

                            if (noEdges)
                            {
                                log.warning("EMPTY FOR: " + followersURLNext);
                            }
                            else
                            {
                                followersListRAW.add(edges.toString());
                                log.info("edges: " + edges.toString());
                                log.info("followerSTRING: " + followersListRAW.size());
                            }
                        }
                        catch (UnsupportedOperationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                log.info("END OF QUERY!");

                LinkedList<Object> payload = new LinkedList<>();
                payload.add(totalFollowers);
                payload.add(followersListRAW);
                log.info(followersListRAW.toString());
                return payload;
            }
            else
            {
                log.info("error");
                log.info(response.toString());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LinkedList<Collection> loadInstFollows(String instUsername) {
        log.info("loadInstFollows");

        try
        {
            LinkedList<Collection> payload = new LinkedList<>();

            Instagram4j instagram = httpClient.Instagram4jBuilder();
            instagram.setup();
            instagram.login();

            InstagramSearchUsernameResult userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(instUsername));

            Thread followersThread = new Thread(() -> {
                try
                {
                    int e = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk())).users.size();
                    log.info("eee " + e);

                    InstagramGetUserFollowersResult githubFollowers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
                    List<InstagramUserSummary> followersUsers = githubFollowers.getUsers();
                    System.out.println();

                    log.info(githubFollowers.getPage_size() + " ");
                    log.info(githubFollowers.getNext_max_id() + " ");
                    log.info(githubFollowers.getCheckpoint_url() + " ");
                    log.info(githubFollowers.getMessage() + " ");
                    log.info(githubFollowers.getStatus() + " ");
                    log.info(githubFollowers.getFeedback_message() + " ");

                    for (InstagramUserSummary user : followersUsers) {
                        System.out.println("User " + user.getUsername() + " follows Github!");
                    }

                    payload.add(followersUsers);
                    log.info("followersUsers size:" + followersUsers.size());

                    /*InstagramUser instagramUser = userResult.getUser();
                    log.info(instagramUser.getHd_profile_pic_url_info().url);*/

                    //List<InstagramUserSummary> followersUsers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk())).getUsers();
                    /*payload.add(followersUsers);
                    log.info("followersUsers size:" + followersUsers.size());*/
                    //InstagramUser instagramUser = userResult.getUser();
                    //log.info(instagramUser.getHd_profile_pic_url_info().url);
                    //instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk(), "500"))

                    /*InstagramGetUserFollowersResult userFollowers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
                    List<InstagramUserSummary> users = userFollowers.getUsers();
                    for (InstagramUserSummary user : users) {
                        System.out.println("User " + user.getUsername() + " follows Github!");
                    }*/
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread followingThread = new Thread(() -> {
                try
                {
                    String mm = "500";
                    List<InstagramUserSummary> usersFollowing = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk())).getUsers();
                    log.info("usersFollowing size:" + usersFollowing.size());

                    payload.add(usersFollowing);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                //InstagramGetUserFollowersResult following = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk()));
                //List<InstagramUserSummary> usersFollowing = following.getUsers();
            });

            followersThread.start();
            followingThread.start();

            try
            {
                followersThread.join();
                followingThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

            return payload;

            /*for (InstagramUserSummary user : followersUsers) {
                System.out.println(instUsername + " follower: " + user.username);
            }*/
            /*for (InstagramUserSummary user : usersFollowing) {
                System.out.println(instUsername + " following: " + user.toString());
            }*/
            //payload.add(followersUsers);
            //payload.add(usersFollowing);
            //return payload;
        }

        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Account instApiAccount(String instUsername) {
        log.info("loadInstProfile");

        try
        {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            Account account = instagram.getAccountByUsername(instUsername);

            //RequestQueue queue = Volley.newRequestQueue(this);
            //httpClient.Instagram4jBuilder().sendRequest(new )
            //instagram.
            //account.get
            //long id
            //instagram.getFollowers(2128921037200382, 1);
            //log.info(account.toString());
            return account;
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public PageObject<Media> loadInstPosts(String instUsername) {
        log.info("loadInstPosts");

        try
        {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            Account account = instagram.getAccountByUsername(instUsername);
            PageObject<Media> medias = account.getMedia();//instagram.getMedias(instUsername, 1);

            //PageObject<Account> followers = instagram.getFollowers(Long.parseLong("17841401343956222"), 1);
            //log.info(followers.toString());
            //account.
            //PageObject<Media> medias = instagram.getMedias(instUsername, 10);
            return medias;
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
        //return null;
    }

    public InstProfile loadInstProfile(String instUsername) {
        log.info(instUsername);
        InstProfile instProfile = new InstProfile();

        Thread instData = new Thread(() -> {

            /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);*/

            OkHttpClient httpClient = this.httpClient.OkHttpClientFactory();//new OkHttpClient.Builder()
                    /*.addNetworkInterceptor(loggingInterceptor)
                    .addInterceptor(new ErrorInterceptor())
                    .cookieJar(new DefaultCookieJar(new CookieHashSet()))
                    .build();*/

            Instagram instagram = new Instagram(httpClient);
            Account account = null;

            try {
                account = instagram.getAccountByUsername(instUsername);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            instProfile.setUsername(account.getUsername());
            instProfile.setBiography(account.getBiography());
            instProfile.setFullName(account.getFullName());
            instProfile.setPic(account.getProfilePicUrl());
            instProfile.setPicFull(account.getProfilePicUrlHd());
            instProfile.setFollowersAmount(account.getFollowedBy());
            instProfile.setFollowingAmount(account.getFollows());


            long instID = account.getId();
            try
            {
                PageObject<Account> followers = instagram.getFollows(instID, 1);
                System.out.println();
                log.info(followers.toString());
                //followers.getNodes().forEach(account1 -> log.info(account1.toString()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            /*PageObject<Media> medias = null;
            try{
                medias = instagram.getMedias(instUsername, 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println();
            log.info("MEDIA");
            log.info(medias.toString());

            PageObject<Account> followers = null;
            PageObject<Account> follows = null;
            try{
                followers = instagram.getFollowers(account.getId(), 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try{
                follows = instagram.getFollows(account.getId(), 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
            log.info(account.getId() + " id");
            log.info(followers.toString());
            List<Account> nodes = followers.getNodes();

            nodes.forEach(account1 -> log.info(account1.toString()));*/
            //log.info(followers.toString());
            //log.info(follows.toString());
            //System.out.println(medias.getNodes().get(0).getDisplayUrl());
            //instProfile.setMedia(medias);
            //instProfile.getMedia().add(medias);
        });


        Thread instFollowers = new Thread(() -> {

            /*Instagram4j instagram = Instagram4j.builder().username("creep_waves").password("Route456123").build();
            instagram.setup();
            try {
                instagram.login();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            InstagramSearchUsernameResult userResult = null;
            try {
                userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(instUsername));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("ID for user is " + userResult.getUser().getPk());
            System.out.println("Number of followers: " + userResult.getUser().getFollower_count());

            InstagramGetUserFollowersResult followers = null;
            try {
                followers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            List<InstagramUserSummary> users = followers.getUsers();

            for (InstagramUserSummary user : users) {
                System.out.println(instUsername + " follower: " + user.toString());
                //System.out.println(instUsername + " follower: " + user.getUsername());
            }

            try {
                InstagramGetUserFollowersResult following = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            List<InstagramUserSummary> usersFollowing = followers.getUsers();

            for (InstagramUserSummary user : usersFollowing) {
                System.out.println(instUsername + " following: " + user.toString());
            }*/


            //instProfile.setFollowers();


        });



        instData.start();
        //instFollowers.start();


        try
        {
            instData.join();
            instFollowers.join();

        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        instRepo.save(instProfile);

        log.info(instProfile.toString());
        log.info("Done!");

        return instProfile;
    }


    public InstProfile loadFollowersListSelenium(String instUsername) {

        log.info("CHROME loadFollowersList");

        /*ChromeOptions options = new ChromeOptions();
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        //logPrefs.enable(LogType.N, Level.ALL);
        cap.setCapability(CapabilityType.SUPPORTS_NETWORK_CONNECTION, logPrefs);*/

        /*PROXY NEW EACH REQUEST!!!*/

        WebDriver driver = new ChromeDriver(/*cap*/);
        driver.manage().window().setPosition(new Point(-2000, 0));

        /*LOGIN INSTAGRAM*/
        {
            String url = "https://www.instagram.com/accounts/login/";
            driver.get(url);

            WebElement login = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
            login.sendKeys("creep_waves");

            WebElement password = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
            password.sendKeys("QwertyRoute01");
            password.sendKeys(Keys.ENTER);
        }

        /*GET FIRST FOLLOWERS DIV*/
        WebElement followersDIV;
        {
            new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("piCib")));
            String url1 = "https://www.instagram.com/" + instUsername;
            driver.get(url1);

            WebElement followersButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, '/" + instUsername + "/followers/')]")));
            followersButton.click();
            driver.switchTo().activeElement();

            followersDIV = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("PZuss")));
        }

        /*Get User Data*/
        Map<String, String> followersData = new HashMap<>(),followingData = new HashMap<>();
        List<WebElement> li = followersDIV.findElements(By.tagName("li"));

        //log.info(driver.getPageSource());
        String followersMeta = StringUtils.substringBetween(driver.getPageSource(),"<meta content=\""," подписчиков,");
        String followingMeta = StringUtils.substringBetween(driver.getPageSource(),"подписчиков, "," подписок,");

        int followersAmount = Integer.parseInt(followersMeta.replaceAll(",",""));
        int followingAmount = Integer.parseInt(followingMeta.replaceAll(",",""));

        log.info("followersMeta: " + followersMeta);
        log.info("followingMeta: " + followingMeta);

        Coordinates cor = ((Locatable) followersDIV).getCoordinates();

        /*SCROLL FOLLOWERS DIV AND FILL FOLLOWERS MAP*/
        {
            while (followersData.size() < followersAmount) {
                try
                {
                    log.info("size: " + followingData.size());
                    cor.inViewPort();
                    ///Thread.sleep(100);

                    ///refreshFollowersDIV();
                    followersDIV = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("PZuss")));
                    li = followersDIV.findElements(By.tagName("li"));

                    li.forEach(webElement -> {
                        List<String> userData = Arrays.asList(webElement.getText().split("\n"));
                        String userName = userData.get(0);
                        String fullName = userData.get(1);

                        System.out.println();
                        log.info("strings: " + userData.toString());
                        log.info("userName: " + userName);
                        log.info("fullName: " + fullName);

                        if (userData.size() == 3) {
                            followersData.putIfAbsent(userName, fullName);
                        } else followersData.putIfAbsent(userName, "");
                        //followersData.putIfAbsent(userName, fullName);
                    });
                    log.info("size: " + followersData.size());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        /*FOLLOWING*/
        {
            //new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("piCib")));
            String url1 = "https://www.instagram.com/" + instUsername;
            driver.get(url1);
            WebElement followersButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, '/" + instUsername + "/following/')]")));
            followersButton.click();
            driver.switchTo().activeElement();
            followersDIV = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("PZuss")));

            cor = ((Locatable) followersDIV).getCoordinates();

            {
                while (followingData.size() < followingAmount) {
                    log.info("size: " + followingData.size());
                    try
                    {
                        cor.inViewPort();

                        followersDIV = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("PZuss")));
                        li = followersDIV.findElements(By.tagName("li"));

                        li.forEach(webElement -> {
                            List<String> userData = Arrays.asList(webElement.getText().split("\n"));
                            String userName = userData.get(0);
                            String fullName = userData.get(1);

                            System.out.println();
                            log.info("strings: " + userData.toString());
                            log.info("userName: " + userName);
                            log.info("fullName: " + fullName);

                            if (userData.size() == 3) {
                                followingData.putIfAbsent(userName, fullName);
                            }
                            else followingData.putIfAbsent(userName, "");
                        });
                        log.info("size: " + followingData.size());
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }


        /*FINAL DATA PROCESSING AND SAVING*/
        InstProfile instProfile = instRepo.findById(instUsername).orElseGet(() -> {
            return new InstProfile(instUsername);
        });

        instProfile.setFollowers(followersData);
        instProfile.setFollowing(followingData);
        instRepo.save(instProfile);
        log.info(instProfile.toString());

        return instProfile;
    }


}
