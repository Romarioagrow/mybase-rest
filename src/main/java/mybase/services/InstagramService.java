package mybase.services;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.InstFollowers;
import mybase.domain.InstProfile;
import mybase.repo.InstFollowersRepo;
import mybase.repo.InstProfileRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Log
@Service
@AllArgsConstructor
public class InstagramService {
    private final InstProfileRepo instRepo;
    private final InstFollowersRepo followersRepo;

    public InstFollowers loadInstFollowersData(Map<String, String> dataToServer) {
        log.warning("Instagram API libraries have been removed. This feature is currently disabled.");
        String username = dataToServer.get("username");
        InstProfile instProfile = instRepo.findByUsername(username);
        return instProfile != null ? instProfile.getInstFollowers() : null;
    }

    public InstFollowers checkFollowersListDB(String username) {
        InstProfile profile = instRepo.findByUsername(username);
        return profile != null ? profile.getInstFollowers() : null;
    }

    public ResponseEntity<?> saveProfileGraph(InstProfile instProfileDATA) {
        try {
            log.info("saveProfileGraph: " + instProfileDATA.toString());

            String instProfileID = instProfileDATA.getUsername();
            InstProfile instProfileDB = instRepo.findByUsername(instProfileID);

            if (instProfileDB == null) {
                return createInstProfile(instProfileDATA);
            } else {
                return updateInstProfile(instProfileDB, instProfileDATA);
            }
        } catch (Exception e) {
            log.warning("Exception saveProfileGraph!");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> updateInstProfile(InstProfile instProfileDB, InstProfile instProfileDATA) {
        log.info("Updating: " + instProfileDATA.toString());

        if (instProfileDB.getInstFollowers() != null) {
            InstFollowers instFollowers = followersRepo.findByInstProfile(instProfileDB);
        }

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

    public InstProfile loadFollowersListSelenium(String instUsername) {
        log.warning("Selenium Instagram scraping is currently disabled.");
        return instRepo.findByUsername(instUsername);
    }

    public Object loadScrapperInstProfile(String instUsername) {
        log.warning("Instagram scraper libraries have been removed. This feature is currently disabled.");
        return null;
    }
}
