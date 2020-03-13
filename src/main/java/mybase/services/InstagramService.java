package mybase.services;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.model.Account;
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

    public InstFollowers/*LinkedList<Object>*/ processFollowers(Map<String, String> dataToServer) {

        /*
         * 1. Объект с полями для:
         * V.Подсисчиков и подписок,
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
        String sessionID = dataToServer.get("sessionid");

        if (followersAvailable(username)) return storedInstFollowers(username);

        OkHttpClient httpClient = buildHttpCookieClient(sessionID);
        InstProfile instProfile = Objects.requireNonNull(instRepo.findByUsername(username));
        LinkedHashMap<String, String> graphApiData =  Objects.requireNonNull(collectGraphApiData(httpClient, username, sessionID));

        Thread collectFollowers = new Thread(() -> collectInstFollowersList(instProfile, graphApiData, httpClient));
        Thread collectFollowing = new Thread(() -> collectInstFollowingList(instProfile, graphApiData, httpClient));
        collectFollowers.start();
        collectFollowing.start();

        try {
            collectFollowers.join();
            collectFollowing.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println();
        System.out.println();
        log.info("RESULT");
        log.info("instProfile: " + instProfile.toString());
        log.info("getInstFollowers: " + instProfile.getInstFollowers().toString());
        log.info("getFollowers: " + instProfile.getInstFollowers().getFollowers().toString());
        log.info("getFollowing: " + instProfile.getInstFollowers().getFollowing().toString());

        /*LinkedList<Object> payload = new LinkedList<>();
        payload.add(instProfile);
        payload.add(instProfile.getInstFollowers());
        return payload;*/
        return instProfile.getInstFollowers();
    }

    public InstFollowers checkFollowersListDB(String username) {
        return instRepo.findByUsername(username).getInstFollowers();
    }

    private InstFollowers storedInstFollowers(String username) {
        return instRepo.findByUsername(username).getInstFollowers();
    }

    private boolean followersAvailable(String username) {
        try
        {
            InstProfile instProfile = instRepo.findByUsername(username);
            if (instProfile != null) {
                boolean hasFollowersDB = !instProfile.getInstFollowers().getFollowers().isEmpty();
                log.info("Profile found in DB: " + username);
                log.info("Followers stored in DB: " + hasFollowersDB);
                return hasFollowersDB;
            }
            else return false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

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
            else return updateInstProfile(instProfileDB, instProfileDATA);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.warning("Exception saveProfileGraph!");
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*private boolean needToChange() {
        return false;
    }*/

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

    private LinkedHashMap<String, String> collectGraphApiData(OkHttpClient httpClient, String username, String sessionID) {
        try
        {
            LinkedHashMap<String, String> graphApiData = new LinkedHashMap<>();

            String userPageInfoUrl = buildUserPageInfoUrl(username);
            Request userPageInfoRequest = new Request.Builder().get().url(userPageInfoUrl).build();

            Response response = buildHttpClient(httpClient, userPageInfoRequest, 3);
            if (response.isSuccessful())
            {
                String responseString = Objects.requireNonNull(response.body()).string();
                JsonElement jsonBody = parseJsonBody(responseString);
                JsonObject userObject = jsonBody.getAsJsonObject().getAsJsonObject("graphql").getAsJsonObject("user");
                String userID = userObject.get("id").getAsString();
                String endCursor1 = userObject.getAsJsonObject().getAsJsonObject("edge_owner_to_timeline_media").getAsJsonObject("page_info").get("end_cursor").getAsString();
                String endCursor2 = userObject.getAsJsonObject().getAsJsonObject("edge_saved_media").getAsJsonObject("page_info").get("end_cursor").getAsString();

                log.info("userObject: " + userObject.toString());
                log.info("userID: " + userID);
                log.info("endCursor1: " + endCursor1);
                log.info("endCursor2: " + endCursor2);

                graphApiData.put("userID", userID);
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

    private void collectInstFollowingList(InstProfile instProfile, LinkedHashMap<String, String> graphApiData, OkHttpClient httpClient) {
        System.out.println();
        log.info("collectInstFollowersList()");
        /// https://www.instagram.com/graphql/query/?query_hash=d04b0a864b4b54837c0d870b0e77e076&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Afalse%2C%22first%22%3A24%7D

        /*INITIALIZATION*/
        final String followingHash = "d04b0a864b4b54837c0d870b0e77e076";
        final String followingURLInitial = "https://www.instagram.com/graphql/query/?query_hash=d04b0a864b4b54837c0d870b0e77e076&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Afalse%2C%22first%22%3A24%7D";
        final String followingPath = "edge_follow";
        log.info("followersURLInitial: " + followingURLInitial);

        int timeout = 3;
        //int totalFollowing = Integer.parseInt(graphApiData.get("totalFollowing"));
        String userID = graphApiData.get("userID");
        String endCursor1 = graphApiData.get("endCursor1");
        String endCursor2 = graphApiData.get("endCursor2");

        HashMap<String, String/*LinkedHashMap<String, String>*/> followingDATA = new LinkedHashMap<>();

        /*FOLLOWING QUERY REQUEST*///FIRST QUERY
        Request followingRequest = new Request.Builder().get().url(followingURLInitial).build();
        try
        {
            Response response = buildHttpClient(httpClient, followingRequest, timeout);
            if (response.isSuccessful())
            {
                //log.info("response: " + response.toString());
                boolean hasNext = true;
                String responseString, endCursor;
                JsonArray edges;
                JsonElement jsonElement;
                JsonObject jsonObject, levelData, levelUser, edge_followed_by, levelInfo;

                jsonElement =  new JsonParser().parse(response.body().string());
                edge_followed_by = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject(followingPath);
                levelInfo = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject(followingPath).getAsJsonObject("page_info");

                hasNext = levelInfo.get("has_next_page").getAsBoolean();
                endCursor = levelInfo.get("end_cursor").getAsString();
                edges = edge_followed_by.getAsJsonArray("edges");

                jsonFollowersExtractor(edges, followingDATA);

                log.info(edge_followed_by.toString());
                log.info(levelInfo.toString());
                log.info("hasNext: " + hasNext);
                log.info("endCursor: " + endCursor);
                log.info("edges: " + edges);

                /*CYCLE NEXT_EDGES QUERIES*/
                while (hasNext)
                {
                    String followingURLNext = makeURIEncoded(followingHash, userID, endCursor);
                    System.out.println();
                    log.info("NEW followingURLNext: " + followingURLNext);

                    followingRequest = new Request.Builder().get().url(followingURLNext).build();
                    Response responseNext = buildHttpClient(httpClient, followingRequest, timeout);
                    if (responseNext.isSuccessful())
                    {
                        try
                        {
                            /*DATA FROM NEW REQUEST*/
                            jsonElement =  new JsonParser().parse(responseNext.body().string());
                            edge_followed_by = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject(followingPath);
                            levelInfo = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject(followingPath).getAsJsonObject("page_info");

                            hasNext = levelInfo.get("has_next_page").getAsBoolean();

                            if (hasNext) {
                                endCursor = levelInfo.get("end_cursor").getAsString();
                                log.info("getNnd_cursorNew: " + levelInfo.get("end_cursor").getAsString());
                                log.info("endCursorNew: " + endCursor);
                            }

                            /*FOLLOWERS EDGES*/
                            edges = edge_followed_by.getAsJsonArray("edges");
                            boolean noEdges = edges.toString().equals("[]");

                            if (noEdges) {
                                log.warning("EMPTY FOR: " + followingURLNext);
                            }
                            else {
                                jsonFollowersExtractor(edges, followingDATA);
                            }
                        }
                        catch (UnsupportedOperationException e) {
                            e.printStackTrace();
                        }
                    }
                }

                instProfile.getInstFollowers().setFollowing(followingDATA);
                instRepo.save(instProfile);

                log.info("TOTAL: " + followingDATA.size());
                log.info("END OF FOLLOWING QUERY!");
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

    }

    public void collectInstFollowersList(InstProfile instProfile, LinkedHashMap<String, String> graphApiData, OkHttpClient httpClient) {
        System.out.println();
        log.info("collectInstFollowersList()");
        ///https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A24%7D

        /*INITIALIZATION*/
        final String followersHash = "c76146de99bb02f6415203be841dd25a";
        final String followersURLInitial = "https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A%221038252798%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A24%7D";
        final String followersPath = "edge_followed_by";
        log.info("followersURLInitial: " + followersURLInitial);

        int timeout = 3;
        int totalFollowers = Integer.parseInt(graphApiData.get("totalFollowers"));
        String userID = graphApiData.get("userID");
        String endCursor1 = graphApiData.get("endCursor1");
        String endCursor2 = graphApiData.get("endCursor2");

        HashMap<String, String/*LinkedHashMap<String, String>*/> followersDATA = new LinkedHashMap<>();

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
                edge_followed_by = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject(followersPath);
                levelInfo = jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject(followersPath).getAsJsonObject("page_info");

                hasNext = levelInfo.get("has_next_page").getAsBoolean();
                endCursor = levelInfo.get("end_cursor").getAsString();
                edges = edge_followed_by.getAsJsonArray("edges");

                jsonFollowersExtractor(edges, followersDATA);

                log.info(edge_followed_by.toString());
                log.info(levelInfo.toString());
                log.info("hasNext: " + hasNext);
                log.info("endCursor: " + endCursor);
                log.info("edges: " + edges);

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

                            /*FOLLOWERS EDGES*/
                            edges = edge_followed_by.getAsJsonArray("edges");
                            boolean noEdges = edges.toString().equals("[]");

                            if (noEdges) {
                                log.warning("EMPTY FOR: " + followersURLNext);
                            }
                            else {
                                jsonFollowersExtractor(edges, followersDATA);
                            }
                        }
                        catch (UnsupportedOperationException e) {
                            e.printStackTrace();
                        }
                    }
                }

                instProfile.getInstFollowers().setFollowers(followersDATA);
                instRepo.save(instProfile);

                log.info("TOTAL: " + followersDATA.size());
                log.info("END OF FOLLOWERS QUERY!");
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
    }

    private void jsonFollowersExtractor(JsonArray edges, HashMap<String, String/*LinkedHashMap<String, String>*/> followersDATA) {
        edges.forEach(jsonElement1 -> {
            HashMap<String, Object> hashMap = Objects.requireNonNull(new Gson().fromJson(jsonElement1, HashMap.class));

            hashMap.forEach((jsonKey, jsonVal) -> {
                LinkedHashMap<String, String> properties = new LinkedHashMap<>();

                String valueToList = StringUtils.substringBetween(jsonVal.toString(),"{", ", reel={id=");
                String followerUsername = StringUtils.substringBetween(valueToList, "username=", ", ");
                List<String> stringsToValue = new ArrayList<String>(Arrays.asList(valueToList.split(", ")));//Arrays.asList(valueToList);

                stringsToValue.forEach(stringVal -> {
                    String followerKey = StringUtils.substringBefore(stringVal, "=");
                    String followerVal = StringUtils.substringAfter(stringVal, "=");
                    properties.put(followerKey, followerVal);
                });

                followersDATA.put(followerUsername, properties.toString()); /// TO_STRING???
            });
        });
        log.info("followers: " + followersDATA.size());
        log.info("followersDATA: " + followersDATA.toString());
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
                        final Request authorized = chain.request().newBuilder()
                                .addHeader("Cookie", sessionID)
                                .build();
                        return chain.proceed(authorized);
                    }
                })
                .build();
    }

    private Response buildHttpClient(OkHttpClient httpClient, Request followersRequest, int timeout) throws IOException {
        return httpClient.newBuilder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build()
                .newCall(followersRequest)
                .execute();
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

    public Account loadScrapperInstProfile(String instUsername) {
        log.info("loadScrapperInstProfile: " + instUsername);

        InstProfile instProfile = new InstProfile();
        Account account = new Account();

        try {
            OkHttpClient httpClient = this.httpClient.OkHttpClientFactory();
            Instagram instagram = new Instagram(httpClient);
            account = instagram.getAccountByUsername(instUsername);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        log.info("account SCRAPPER: " + account.toString());
        log.info("Done!");
        return account;
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
                }
                catch (Exception ex) {
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

    /*FREE_API*/
    /*public LinkedList<Collection> loadInstFollows(String instUsername) {
        log.info("loadInstFollows: " + instUsername);
        try {
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
                    *//*InstagramUser instagramUser = userResult.getUser();
                    log.info(instagramUser.getHd_profile_pic_url_info().url);*//*
                    //List<InstagramUserSummary> followersUsers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk())).getUsers();
                    *//*payload.add(followersUsers);
                    log.info("followersUsers size:" + followersUsers.size());*//*
                    //InstagramUser instagramUser = userResult.getUser();
                    //log.info(instagramUser.getHd_profile_pic_url_info().url);
                    //instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk(), "500"))
                    *//*InstagramGetUserFollowersResult userFollowers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
                    List<InstagramUserSummary> users = userFollowers.getUsers();
                    for (InstagramUserSummary user : users) {
                        System.out.println("User " + user.getUsername() + " follows Github!");
                    }*//*
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Thread followingThread = new Thread(() -> {
                try {
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
            try {
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
        try {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            return instagram.getAccountByUsername(instUsername);
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }
    public PageObject<Media> loadInstPosts(String instUsername) {
        System.out.println();
        log.info("!!!loadInstPosts: " + instUsername);
        try {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            Account account = instagram.getAccountByUsername(instUsername);
            log.info(account.getMedia().toString());
            return account.getMedia();
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }*/
}
