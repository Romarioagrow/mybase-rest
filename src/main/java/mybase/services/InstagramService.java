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
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
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
import java.net.URISyntaxException;
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



    public LinkedList<Object> httpClientRequester(Map<String, String> dataToServer) {
        log.info("RestTemplate");

        /*
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = "https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A201512132%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A50%2C%22after%22%3A%22QVFENWU0Sl9kd2hVVDRYN1E4RHUycVBkU1JRMVlvYWl2UXd0ejRlMEZRTDE4VUt4RGl6dzB6OUl6eUhONmp3UHRSZmFoWW5mWDZWeDB3N2hQbEtkbEpjQQ%3D%3D%22%7D";
        //formData.add("app_id", "226365095211205");
        log.info(url);
        HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(formData, headers);*/

        //ResponseEntity<String> response = restTemplate.get( url, new HttpEntity<MultiValueMap<String, String>>(formData, headers) , String.class );
        /*ResponseEntity<String> response = restTemplate.getForEntity( url, new HttpEntity<MultiValueMap<String, String>>(formData, headers) , String.class );
        String toString = response.toString();
        log.info(toString);*/

       /* Map<String, Map<String, String>> config = new HashMap<>();
        HashMap<String, String> followers = new HashMap<>();
        followers.put("hash", "c76146de99bb02f6415203be841dd25a");
        followers.put("path", "edge_followed_by");
        HashMap<String, String> following = new HashMap<>();
        following.put("hash", "d04b0a864b4b54837c0d870b0e77e076");
        following.put("path", "edge_follow");
        config.put("followers", followers);
        config.put("following", following);

        String userPageUrl = "https://www.instagram.com/" + instUsername + "/?__a=1";
        String userID = "201512132";*/
        //String url = "https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=%7B%22id%22%3A201512132%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A50%2C%22after%22%3A%22QVFENWU0Sl9kd2hVVDRYN1E4RHUycVBkU1JRMVlvYWl2UXd0ejRlMEZRTDE4VUt4RGl6dzB6OUl6eUhONmp3UHRSZmFoWW5mWDZWeDB3N2hQbEtkbEpjQQ%3D%3D%22%7D";

        /*followers: {
            hash: 'c76146de99bb02f6415203be841dd25a',
                    path: 'edge_followed_by'
        },
        followings: {
            hash: 'd04b0a864b4b54837c0d870b0e77e076',
                    path: 'edge_follow'
        }*/

        ///
        HashMap<String, String> followers = new HashMap<>(), following = new HashMap<>();
        followers.put("hash", "c76146de99bb02f6415203be841dd25a");
        followers.put("path", "edge_followed_by");
        following.put("hash", "1038252798%3AwoM4qyrNWkOxkq%3A11");
        following.put("path", "edge_follow");

        ///
        String followersURL = dataToServer.get("followersURL");
        String userID = dataToServer.get("userID");
        String sessionID = "sessionid=1038252798%3AnmyKoPJVjOIMKL%3A16;";

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

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("somParam", "someValue")
                .build();

        Request request = new Request.Builder().get().url(followersURL).build();
        try
        {
            Response response = httpClient.newBuilder()
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .execute();
            if (response.isSuccessful())
            {
                log.info("response.toString: " + response.toString());

                JsonElement jelement;
                JsonObject jsonObject, levelData, levelUser, edge_followed_by, levelInfo;

                boolean hasNext = true;
                int totalFollowers;
                JsonArray edges;
                String responseString, endCursor;//// query, queryToEncode, encodeQuery, finalQueryURL;

                responseString = response.body().string();
                jelement = new JsonParser().parse(responseString);
                jsonObject = jelement.getAsJsonObject();
                levelData = jsonObject.getAsJsonObject("data");

                //log.info(levelInfo.toString());


                levelUser = levelData.getAsJsonObject("user");
                edge_followed_by = levelUser.getAsJsonObject("edge_followed_by");
                levelInfo = edge_followed_by.getAsJsonObject("page_info");

                log.info(levelInfo.toString());


                hasNext = levelInfo.get("has_next_page").getAsBoolean();
                endCursor = levelInfo.get("end_cursor").getAsString();
                totalFollowers = edge_followed_by.get("count").getAsInt();
                edges = edge_followed_by.getAsJsonArray("edges");

                log.info("responseString" + responseString);
                log.info(levelData.toString());
                log.info(levelUser.toString());
                log.info(edge_followed_by.toString());
                log.info(levelInfo.toString());
                log.info(hasNext + "");
                log.info(endCursor + "");
                log.info("totalFollowers: " + totalFollowers);
                log.info("hash: " + followers.get("hash"));
                log.info("edges: " + edges.toString());

                LinkedList<String> followersSTRING = new LinkedList<>();
                followersSTRING.add(edges.toString());

                while (hasNext/*followersSTRING.size() < totalFollowers*/) {

                    String query =
                            "https://www.instagram.com/graphql/query/?query_hash=" + followers.get("hash") + "&variables=";
                    String queryToEncode =
                            "{\"id\":" + userID + ",\"include_reel\":true,\"fetch_mutual\":true,\"first\":30,\"after\":\"" + endCursor + "\"}";
                    String encodeQuery = URLEncoder.encode(queryToEncode, Charset.defaultCharset());
                    String finalQueryURL = query + encodeQuery;

                    log.info("queryToEncode: " + queryToEncode);
                    log.info("encodeQuery: " + encodeQuery);
                    log.info("finalQuery: " + finalQueryURL);

                    request = new Request.Builder().get().url(finalQueryURL).build();
                    Response responseNext = httpClient.newBuilder()
                            .readTimeout(3, TimeUnit.SECONDS)
                            .build()
                            .newCall(request)
                            .execute();
                    if (responseNext.isSuccessful()) {
                        try
                        {
                            /*DATA FROM NEW REQUEST*/
                            responseString = responseNext.body().string();
                            jelement = new JsonParser().parse(responseString);
                            jsonObject = jelement.getAsJsonObject();
                            levelData = jsonObject.getAsJsonObject("data");
                            levelUser = levelData.getAsJsonObject("user");
                            edge_followed_by = levelUser.getAsJsonObject("edge_followed_by");
                            levelInfo = edge_followed_by.getAsJsonObject("page_info");

                            hasNext = levelInfo.get("has_next_page").getAsBoolean();

                            if (hasNext) {
                                endCursor = levelInfo.get("end_cursor").getAsString();
                                edges = edge_followed_by.getAsJsonArray("edges");

                                followersSTRING.add(edges.toString());
                                log.info("followerSTRING: " + followersSTRING.size());
                            }
                            else
                            {
                                log.info("END OF QUERY!");
                                hasNext = false;
                            }
                        }
                        catch (UnsupportedOperationException e) {
                            e.printStackTrace();
                        }
                    }
                }


                LinkedList<Object> payload = new LinkedList<>();


                payload.add(totalFollowers);
                payload.add(followersSTRING);

                log.info(followersSTRING.toString());
                return payload;



                /*hasNext = response.data[0]
                endCursor = response.data[1]
                totalFollowers = response.data[2]
                followersSTRING = response.data[3]
                followersList.push(JSON.parse(followersSTRING))
                console.log(followersList)
                while (followersList.length < totalFollowers) {

                    let followersURL = `https://www.instagram.com/graphql/query/?query_hash=${config['followers'].hash}&variables=${encodeURIComponent(JSON.stringify({
                    "id": userID,
                            "include_reel": true,
                            "fetch_mutual": true,
                            "first": 50,
                            "after": endCursor
                }))}`
            console.log('followersURL: ' + followersURL)

            dataToServer.followersURL = followersURL

            axios.post('/api/profile/instagram/restRequests', dataToServer, configJson).then(value => {
                    hasNext = response.data[0]
            if (hasNext) {
                endCursor = response.data[1]
                //followersSTRING = response.data[3]
                followersList.push(JSON.parse(response.data[3]))
            }
                        })

            console.log(followersList.length)*/


                /*while (hasNext) {
                 *//*String encodedString = new URIBuilder()
                            .setParameter("id", userID)
                            .setParameter("include_reel", "true")
                            .setParameter("fetch_mutual", "true")
                            .setParameter("first", "50")
                            .setParameter("after", endCursor)
                            .build()
                            .getRawQuery(); // output: i=encodedString
                    String finalQuery = query.concat(encodedString);
                    log.info(finalQuery);*//*
             *//*followersURL =
                            "https://www.instagram.com/graphql/query/?query_hash=${config['followers'].hash}&variables=${encodeURIComponent(JSON.stringify({
                    "id": userID,
                            "include_reel": true,
                            "fetch_mutual": true,
                            "first": 50,
                            "after": after
                }))}`*//*
                }*/



            }
            else
            {
                log.info("error");
            }
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        return null;

        // create an instance of RestTemplate
        /*RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //headers.set("X-Request-Source", "Desktop");
        HttpEntity request = new HttpEntity(headers);
        *//*ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class, 1
        );*//*
        log.info(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // check response
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());
        }
        else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }*/
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

            /*System.out.println("ID for user is " + userResult.getUser().getPk());
            System.out.println("Number of followers: " + userResult.getUser().getFollower_count());*/

            Thread followersThread = new Thread(() -> {
                try
                {
                    int e = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk())).users.size();
                    //int dd = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk())).getUsers();
                    log.info("eee " + e);
                    //log.info("eee " + e);

                    InstagramGetUserFollowersResult githubFollowers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
                    List<InstagramUserSummary> followersUsers = githubFollowers.getUsers();
                    System.out.println();
                    System.out.println();
                    //githubFollowers.users().
                    //log.info("getCheckpoint_url " +githubFollowers.getCheckpoint_url() + " ");

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
