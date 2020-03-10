package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import mybase.domain.InstProfile;
import mybase.repo.InstRepo;
import mybase.services.InstagramService;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;

@Log
@Data
@RestController
@AllArgsConstructor
@RequestMapping("/api/profile")
public class InstagramApiController {
    private final InstagramService profileService;
    private final InstRepo instRepo;

    @PostMapping("/instagram/restRequests")
    private void restRequests() {
        profileService.httpClientRequester();
    }

    @PostMapping("/instagram/loadFollowersList")
    private InstProfile loadFollowersList(@RequestBody String instUsername) throws AWTException {
        return profileService.loadFollowersListSelenium(instUsername);
    }

    @PostMapping("/instagram/loadInstFollows")
    private LinkedList<Collection> loadInstFollows(@RequestBody String instUsername) {
        return profileService.loadInstFollows(instUsername);
    }

    @PostMapping("/instagram/loadInstPosts")
    private PageObject<Media> loadInstPosts(@RequestBody String instUsername) {
        return profileService.loadInstPosts(instUsername);
    }

    @PostMapping("/instagram/loadInstProfile")
    private Account loadInstProfile(@RequestBody String instUsername) {
        return profileService.instApiAccount(instUsername);
    }
}
