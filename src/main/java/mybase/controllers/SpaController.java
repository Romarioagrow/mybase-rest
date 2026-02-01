package mybase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to forward SPA routes to index.html
 * This allows Vue Router to handle client-side routing
 */
@Controller
public class SpaController {

    /**
     * Forward all non-API, non-static routes to index.html for SPA routing
     */
    @GetMapping({
        "/oauth2/redirect",
        "/auth",
        "/profile",
        "/posts",
        "/instagram",
        "/spending",
        "/thelogs",
        "/goals",
        "/mybase"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
