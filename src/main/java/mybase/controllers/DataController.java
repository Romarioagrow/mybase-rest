package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.MainUser;
import mybase.services.DataService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log
@RestController
@AllArgsConstructor
@RequestMapping("/api/data")
public class DataController {
    private final DataService dataService;

    @GetMapping("/loadSpendingItems")
    private Map<String, Object> loadSpendingItems(@AuthenticationPrincipal MainUser user) {
        return dataService.allUserSpending(user.getUserID());
    }

    @PostMapping("/addNewSpendingItem")
    private Map<String, Object> addNewSpendingItem(@AuthenticationPrincipal MainUser user, @RequestBody Map<String, String> spendingData
    ){
        return dataService.addNewSpendingItem(user.getUserID(), spendingData);
    }

    @PostMapping("/deleteSpendingItem")
    private Map<String, Object> deleteSpendingItem(@AuthenticationPrincipal MainUser user, @RequestBody Integer spendingID
    ){
        return dataService.deleteSpendingItem(user.getUserID(), spendingID);
    }
}
