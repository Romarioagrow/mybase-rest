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
import mybase.domain.InstFollowers;
import mybase.domain.InstProfile;
import mybase.repo.InstFollowersRepo;
import mybase.repo.InstProfileRepo;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log
@Service
@AllArgsConstructor
public class InstagramService {
    private final InstProfileRepo instRepo;
    private final InstFollowersRepo followersRepo;
    private final WebMvcConfig httpClient;


    /*
     * Отедльно вывод инфо
     * Отдельно вывод постов
     * Отдельно вывод подпсичиков
     * Отдельно сборка профиля в базу
     *
     *
     * */


    public ResponseEntity<?> saveProfileGraph(InstProfile instProfileDATA) {
        try
        {
            log.info("saveProfileGraph: " + instProfileDATA.toString());
            log.info(instProfileDATA.toString());

            String instProfileID = instProfileDATA.getUsername();
            InstProfile instProfileDB = instRepo.findByUsername(instProfileID);

            if (instProfileDB == null) {
                return createInstProfile(instProfileDATA);
            }
            else {
                return updateInstProfile(instProfileDB, instProfileDATA);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            log.warning("Exception saveProfileGraph!");
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean needToChange() {
        return false;
    }

    private ResponseEntity<?> updateInstProfile(InstProfile instProfileDB, InstProfile instProfileDATA) {
        log.info("Updating: " + instProfileDATA.toString());
        /*FOR FUTURE SECURITY OPERATION*/

        if (instProfileDB.getInstFollowers() != null) {
            InstFollowers instFollowers = followersRepo.findByInstProfile(instProfileDB);
            /*FUTURE FOLLOWERS_LIST UPDATE*/
        }

        /// CHECKING AND UPDATING FIELDS ()
        instProfileDB.setLastDataChange(LocalDateTime.now());
        instRepo.save(instProfileDB);
        return new ResponseEntity<>("Updated InstProfile: " + instProfileDB.toString(), HttpStatus.OK);
    }

    private ResponseEntity<?> createInstProfile(InstProfile newInstProfile) {

        newInstProfile.setLastDataChange(LocalDateTime.now());
        newInstProfile.setInstFollowers(new InstFollowers());

        instRepo.save(newInstProfile);
        log.info("Creating: " + newInstProfile.toString());
        return new ResponseEntity<>("Created InstProfile: " + newInstProfile.toString(), HttpStatus.OK);
    }


    public InstProfile processFollowers(Map<String, String> dataToServer) {

        /*
         * 1. Объект с полями для:
         * Подсисчиков и подписок,
         * Обновленный и прошлый список подписчиков,
         * Список отписавшихся и подписавшихся,
         * Список на кого не подписанв ответ и кто не подписан в ответ,
         *
         * Объект для Главного InstProfile, логин по username
         *
         *
         * */

        /*LOAD USER DATA*/
        String username = dataToServer.get("username");
        String sessionID = dataToServer.get("sessionid");//"sessionid=1038252798%3AAbjYDoDJfK6hwQ%3A13;"; ///dataToServer.get("sessionid");

        OkHttpClient httpClient = buildHttpCookieClient(sessionID);
        LinkedHashMap<String, String> graphApiData = collectGraphApiData(httpClient, username, sessionID);

        InstProfile instProfile = Objects.requireNonNull(instRepo.findByUsername(username));
       /* InstFollowers instFollowers = Objects.requireNonNull(instProfile.getInstFollowers());*/


        /*DOWNLOAD FOLLOWERS*/
        //LinkedList<String> followers;// = getFollowersListsAndDataProcess(username);
        //LinkedList<String> following;// = getFollowersListsAndDataProcess(username);

        Thread collectFollowers = new Thread(() -> {
            collectInstFollowersList(instProfile, graphApiData, httpClient);
        });

        Thread collectFollowing = new Thread(() -> {
            //collectInstFollowingList(instProfile, graphApiData, httpClient);

        });

        collectFollowing.start();
        collectFollowers.start();

        try {
            collectFollowing.join();
            collectFollowers.join();

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info(instProfile.toString());
        log.info("END OF ACTION!!!");


        /// processFollowersData();
        //Object UserFollowersList;







        //return collectInstFollowersList(instFollowers, username, dataToServer);
        return instProfile;

    }

    private LinkedHashMap<String, String> collectGraphApiData(OkHttpClient httpClient, String username, String sessionID) {
        try
        {
            LinkedHashMap<String, String> graphApiData = new LinkedHashMap<>();

            String userPageInfoUrl = buildUserPageInfoUrl(username);
            Request userPageInfoRequest = new Request.Builder().get().url(userPageInfoUrl).build();

            Response response = buildHttpClient(httpClient, userPageInfoRequest, 3);
            if (response.isSuccessful())
            {
                ///log.info("userPageInfoResponse: " + response.toString());
                String responseString = Objects.requireNonNull(response.body()).string();
                JsonElement jsonBody = parseJsonBody(responseString);
                JsonObject userObject = jsonBody.getAsJsonObject().getAsJsonObject("graphql").getAsJsonObject("user");
                String userID = userObject.get("id").getAsString();
                String totalFollowers = userObject.getAsJsonObject().getAsJsonObject("edge_followed_by").get("count").getAsString();
                String endCursor1 = userObject.getAsJsonObject().getAsJsonObject("edge_owner_to_timeline_media").getAsJsonObject("page_info").get("end_cursor").getAsString();
                String endCursor2 = userObject.getAsJsonObject().getAsJsonObject("edge_saved_media").getAsJsonObject("page_info").get("end_cursor").getAsString();

                log.info("userObject: " + userObject.toString());
                log.info("totalFollowers: " + totalFollowers);
                log.info("userID: " + userID);
                log.info("endCursor1: " + endCursor1);
                log.info("endCursor2: " + endCursor2);

                graphApiData.put("userID", userID);
                graphApiData.put("totalFollowers", totalFollowers);
                ///graphApiData.put("totalFollowing", Following);
                graphApiData.put("sessionID", sessionID);
                graphApiData.put("endCursor1", endCursor1);
                graphApiData.put("endCursor2", endCursor2);

                return graphApiData;

                /*FOR AUTO-PARSE INITIAL URL*/
                /*followersURLInitial = makeURIEncoded(followersHash, userID, endCursor2);
                log.info("followersURLInitial: " + followersURLInitial);*/
            }
            else
            {
                log.warning("Error in userPage request");
                return null;
            }
        }
        catch (IOException | NullPointerException e) {
            log.warning("Exception in userPage request");
            e.printStackTrace();
            return null;
        }
    }

    private void collectInstFollowingList(InstFollowers instFollowers, LinkedHashMap<String, String> username, OkHttpClient sessionID) {



    }


    public void/*LinkedList<String>*/ collectInstFollowersList(InstProfile instProfile, LinkedHashMap<String, String> graphApiData, OkHttpClient httpClient/*String username, String sessionID*//*Map<String, String> dataToServer*//*, LinkedList<String> followersListRAW*/) {


        //InstFollowers instFollowers = Objects.requireNonNull(instProfile.getInstFollowers());


        System.out.println();
        log.info("RestTemplate");
        ///https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A24%7D

        /*INITIALIZATION*/
        final String followersHash = "c76146de99bb02f6415203be841dd25a";
        final String followersURLInitial = "https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A24%7D";
        log.info("followersURLInitial: " + followersURLInitial);

        /*final String followingHash = "1038252798%3AwoM4qyrNWkOxkq%3A11";
        final String followersPath = "edge_followed_by";
        final String followingPath = "edge_follow";*/

        int timeout = 3;
        int totalFollowers = Integer.parseInt(graphApiData.get("totalFollowers"));
        String userID = graphApiData.get("userID");
        String endCursor1 = graphApiData.get("endCursor1");
        String endCursor2 = graphApiData.get("endCursor2");


        //String username = dataToServer.get("username");
//        String sessionID = "sessionid=1038252798%3AAbjYDoDJfK6hwQ%3A13;"; ///dataToServer.get("sessionid");

        LinkedList<String> followersListRAW = new LinkedList<>();

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

                /*CYCLE NEXT_EDGES QUERIES*/
                while (hasNext)
                {
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

                            if (noEdges) {
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


                /*LinkedList<Object> payload = new LinkedList<>();
                payload.add(totalFollowers);
                payload.add(followersListRAW);
                log.info(followersListRAW.toString());*/
                //return followersListRAW;
                log.info("END OF FOLLOWERS QUERY!");
                instProfile.getInstFollowers().setFollowers(followersListRAW);
                instRepo.save(instProfile);
                log.info(instProfile.toString());
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
        //return null;
    }

    private JsonElement parseJsonBody(String responseString) throws IOException, NullPointerException, IllegalStateException {
        //String responseBody = Objects.requireNonNull(response.body()).string();
        //String responseBody = response.body().string();

        return new JsonParser().parse(responseString);
    }

    private String buildUserPageInfoUrl(String username) {
        return "https://www.instagram.com/" + username + "/?__a=1";
    }

    private OkHttpClient buildHttpCookieClient(String sessionID) {
        return new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //final Request original = chain.request();
                        final Request authorized = chain.request().newBuilder()
                                .addHeader("Cookie", sessionID)
                                .build();
                        return chain.proceed(authorized);
                    }
                })
                .build();
    }

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






    /*FREE_API*/
    public LinkedList<Collection> loadInstFollows(String instUsername) {
        log.info("loadInstFollows: " + instUsername);

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
        }

        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Account instApiAccount(String instUsername) {
        log.info("loadInstProfile: " + instUsername);

        try
        {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            //RequestQueue queue = Volley.newRequestQueue(this);
            //httpClient.Instagram4jBuilder().sendRequest(new )
            //instagram.
            //account.get
            //long id
            //instagram.getFollowers(2128921037200382, 1);
            //log.info(account.toString());
            return instagram.getAccountByUsername(instUsername);
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public PageObject<Media> loadInstPosts(String instUsername) {
        log.info("loadInstPosts: " + instUsername);


        try
        {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            Account account = instagram.getAccountByUsername(instUsername);
            return account.getMedia();
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public InstProfile loadInstProfile(String instUsername) {
        log.info(instUsername);
        InstProfile instProfile = new InstProfile();

        Thread instData = new Thread(() -> {
            /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);*/

            OkHttpClient httpClient = this.httpClient.OkHttpClientFactory();
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

            /*instProfile.setUsername(account.getUsername());
            instProfile.setBiography(account.getBiography());
            instProfile.setFullName(account.getFullName());
            instProfile.setPic(account.getProfilePicUrl());
            instProfile.setPicFull(account.getProfilePicUrlHd());
            instProfile.setFollowersAmount(account.getFollowedBy());
            instProfile.setFollowingAmount(account.getFollows());*/

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
        });


        Thread instFollowers = new Thread(() -> {
            /**/
        });

        instData.start();
        instFollowers.start();

        try {
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

        /*instProfile.setFollowers(followersData);
        instProfile.setFollowing(followingData);*/
        instRepo.save(instProfile);
        log.info(instProfile.toString());

        return instProfile;
    }



}
