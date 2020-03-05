package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import mybase.config.WebMvcConfig;
import mybase.domain.InstProfile;
import mybase.repo.InstRepo;
import okhttp3.OkHttpClient;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Log
@Service
@AllArgsConstructor
public class ProfileService {
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

    public LinkedList<Collection> loadInstFollows(String instUsername) {

        try
        {
            LinkedList<Collection> payload = new LinkedList<>();

            Instagram4j instagram = httpClient.Instagram4jBuilder();//Instagram4j.builder().username("creep_waves").password("Route456123").build();
            instagram.setup();
            instagram.login();

            InstagramSearchUsernameResult userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(instUsername));

            /*System.out.println("ID for user is " + userResult.getUser().getPk());
            System.out.println("Number of followers: " + userResult.getUser().getFollower_count());*/

            Thread followersThread = new Thread(() -> {
                try
                {
                    List<InstagramUserSummary> followersUsers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk())).getUsers();
                    payload.add(followersUsers);
                    log.info("followersUsers size:" + followersUsers.size());

                    InstagramUser instagramUser = userResult.getUser();
                    log.info(instagramUser.getHd_profile_pic_url_info().url);

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
        try
        {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            Account account = instagram.getAccountByUsername(instUsername);

            //log.info(account.toString());
            return account;
        }
        catch (IOException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public PageObject<Media> loadInstPosts(String instUsername) {

        try
        {
            Instagram instagram = new Instagram(httpClient.OkHttpClientFactory());
            Account account = instagram.getAccountByUsername(instUsername);

            //log.info(account.toString());
            //account.getMedia()

            PageObject<Media> medias = account.getMedia();//instagram.getMedias(instUsername, 1);
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
            //System.out.println(account.getMedia().getCount());

            /*log.info(account.getUsername());
            log.info(account.getBiography());
            log.info(account.getFullName());
            log.info(account.getBlockedByViewer());
            log.info(account.getProfilePicUrlHd());
            log.info(account.getFollowedBy() + "f");
            log.info(account.getFollows() + "follows");*/

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
            try
            {
                medias = instagram.getMedias(instUsername, 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //medias = instagram.

            //medias.fo


            System.out.println();
            log.info("MEDIA");
            log.info(medias.toString());

            PageObject<Account> followers = null;
            PageObject<Account> follows = null;
            try
            {
                followers = instagram.getFollowers(account.getId(), 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            try
            {
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



}
