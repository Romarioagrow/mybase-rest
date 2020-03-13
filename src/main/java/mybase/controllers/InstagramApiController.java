package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import mybase.domain.InstProfile;
import mybase.repo.InstProfileRepo;
import mybase.services.InstagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;

@Log
@Data
@RestController
@AllArgsConstructor
@RequestMapping("/api/social/instagram")
public class InstagramApiController {
    private final InstagramService profileService;
    private final InstProfileRepo instRepo;


    /*GRAPH*/
    @PostMapping("/graph/processFollowers")
    private InstProfile processFollowers(@RequestBody Map<String, String> dataToServer) {
        return profileService.processFollowers(dataToServer);
    }
    @PostMapping("/graph/save_profile")
    private ResponseEntity<?> saveProfileGraph(@RequestBody InstProfile instProfile) {
        return profileService.saveProfileGraph(instProfile);
    }


    /*SELENIUM*/
    @PostMapping("/selenium/loadFollowersList")
    private InstProfile loadFollowersList(@RequestBody String instUsername) throws AWTException {
        return profileService.loadFollowersListSelenium(instUsername);
    }


    /*SCRAPPER*/
    @PostMapping("/scraper/loadInstProfile")
    private Account loadInstProfile(@RequestBody String instUsername) {
        return profileService.loadScrapperInstProfile(instUsername);
    }
}
