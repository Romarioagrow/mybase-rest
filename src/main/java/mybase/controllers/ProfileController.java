package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import mybase.domain.InstProfile;
import mybase.services.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Data
@RestController
@AllArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/instagram/loadInstProfile")
    private InstProfile loadInstProfile(@RequestBody String instUsername) {

        return profileService.loadInstProfile(instUsername);
    }
}
