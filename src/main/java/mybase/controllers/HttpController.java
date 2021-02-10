package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import mybase.domain.UserAccount;
import mybase.services.HttpService;
import mybase.services.MediaApiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Log
@Controller
@AllArgsConstructor
public class HttpController {
    private final MediaApiService mediaApiService;
    private final HttpService httpService;


    @GetMapping("/")
    private String urlLogger(HttpServletRequest request, @AuthenticationPrincipal UserAccount userAccount) {

        httpService.checkUserAccount(userAccount);

        log.info("GET /");

        ///mediaApiService.urlLogger(request);

        return "index";
    }
}
