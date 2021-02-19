package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.model.Account;
import mybase.domain.InstFollowers;
import mybase.domain.InstProfile;
import mybase.repo.InstProfileRepo;
import mybase.services.InstagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log
@Data
@RestController
@AllArgsConstructor
@RequestMapping("/api/social/instagram")
public class InstagramApiController {
    private final InstagramService profileService;
    private final InstProfileRepo instRepo;




    /*GRAPH*/
    @PostMapping("/graph/loadInstFollowersListsDB")
    private InstFollowers loadInstFollowersListsDB(@RequestBody Map<String, String> dataToServer) {
        try {
            String username = dataToServer.get("username");
            InstProfile instProfile = instRepo.findByUsername(username);


            

            log.info("username: " + username);
            log.info("instProfile: " + instProfile.toString());

            if (instProfile.hasStoredFollowers()) {
                log.info("Loading from DB for user: " + username);
                return instProfile.getInstFollowers();
            }
            else {
                log.info("Loading from API for user: " + username);
                return loadInstFollowersData(dataToServer);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/graph/loadInstFollowersData")
    private InstFollowers loadInstFollowersData(@RequestBody Map<String, String> dataToServer) {
        return profileService.loadInstFollowersData(dataToServer);
    }
    @PostMapping("/graph/save_profile")
    private ResponseEntity<?> saveProfileGraph(@RequestBody InstProfile instProfile) {
        return profileService.saveProfileGraph(instProfile);
    }
    @PostMapping("/graph/checkFollowersListDB")
    private InstFollowers checkFollowersListDB(@RequestBody String username) {
        return profileService.checkFollowersListDB(username);
    }


    /*SELENIUM*/
    @PostMapping("/selenium/loadFollowersList")
    private InstProfile loadFollowersList(@RequestBody String instUsername) {
        return profileService.loadFollowersListSelenium(instUsername);
    }


    /*SCRAPPER*/
    @PostMapping("/scraper/loadInstProfile")
    private Account loadInstProfile(@RequestBody String instUsername) {
        log.info("Scraper loadInstProfile:" + instUsername.toString());
        return profileService.loadScrapperInstProfile(instUsername);
    }
}
