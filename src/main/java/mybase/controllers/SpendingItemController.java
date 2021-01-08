package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.AccountUser;
import mybase.domain.GoogleAuthUser;
import mybase.services.SpendingItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/data")
public class SpendingItemController {
    private final SpendingItemService itemService;

    private static final String hardcodedUserID = "hardcodeID0";

    @GetMapping("/loadSpendingItems")
    private Map<String, Object> loadSpendingItems(@AuthenticationPrincipal GoogleAuthUser user) {
        if (user != null) {
            return itemService.loadAllUserSpending(user.getUserID());
        }
        else {
            return itemService.loadAllUserSpending(hardcodedUserID);
        }
    }

    @PostMapping("/addNewSpendingItem")
    private Map<String, Object> addNewSpendingItem(@AuthenticationPrincipal AccountUser user,
                                                   @RequestBody Map<String, String> spendingData
    ){
        return itemService.addNewSpendingItem(user, spendingData);
    }

    @PostMapping("/deleteSpendingItem")
    private Map<String, Object> deleteSpendingItem(@AuthenticationPrincipal GoogleAuthUser user,
                                                   @RequestBody Integer spendingID
    ){
        return itemService.deleteSpendingItem(user.getUserID(), spendingID);
    }

     /*@PostMapping("/instProfile")
    private void instProfile(){
        dataService.instProfile();
    }

    @PostMapping("/instFollowers")
    private void instFollowers() throws IOException {
        dataService.instFollowers();
    }*/
}
