package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.cookie.CookieHashSet;
import me.postaddict.instagram.scraper.cookie.DefaultCookieJar;
import me.postaddict.instagram.scraper.interceptor.ErrorInterceptor;
import me.postaddict.instagram.scraper.model.Account;
import mybase.domain.InstProfile;
import mybase.repo.InstRepo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log
@Service
@AllArgsConstructor
public class ProfileService {
    private final InstRepo instRepo;



    public InstProfile loadInstProfile(String instUsername) {
        log.info(instUsername);


        InstProfile instProfile = new InstProfile();



        Thread instData = new Thread(() -> {

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(loggingInterceptor)
                    .addInterceptor(new ErrorInterceptor())
                    .cookieJar(new DefaultCookieJar(new CookieHashSet()))
                    .build();

            Instagram instagram = new Instagram(httpClient);
            Account account = null;

            try {
                account = instagram.getAccountByUsername(instUsername);
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

            instProfile.setUsername(account.getUsername());
            instProfile.setBiography(account.getBiography());
            instProfile.setFullName(account.getFullName());
            instProfile.setPic(account.getProfilePicUrl());
            instProfile.setPicFull(account.getProfilePicUrlHd());
            instProfile.setFollowersAmount(account.getFollowedBy());
            instProfile.setFollowingAmount(account.getFollows());
        });


        Thread instFollowers = new Thread(() -> {


        });



        instData.start();
        instFollowers.start();


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
