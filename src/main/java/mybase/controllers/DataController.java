package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.GoogleAuthUser;
import mybase.services.DataService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/data")
public class DataController {
    private final DataService dataService;

    @PostMapping("/instProfile")
    private void instProfile(){
        dataService.instProfile();
    }

    @PostMapping("/instFollowers")
    private void instFollowers() throws IOException {
        dataService.instFollowers();
    }

    @GetMapping("/loadSpendingItems")
    private Map<String, Object> loadSpendingItems(@AuthenticationPrincipal GoogleAuthUser user) {
        return dataService.allUserSpending(user.getUserID());
    }

    @PostMapping("/addNewSpendingItem")
    private Map<String, Object> addNewSpendingItem(@AuthenticationPrincipal GoogleAuthUser user, @RequestBody Map<String, String> spendingData
    ){
        return dataService.addNewSpendingItem(user.getUserID(), spendingData);
    }

    @PostMapping("/deleteSpendingItem")
    private Map<String, Object> deleteSpendingItem(@AuthenticationPrincipal GoogleAuthUser user, @RequestBody Integer spendingID
    ){
        return dataService.deleteSpendingItem(user.getUserID(), spendingID);
    }
}
