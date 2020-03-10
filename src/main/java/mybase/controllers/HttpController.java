package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Log
@Controller
@AllArgsConstructor
public class HttpController {

    @GetMapping("/")
    private String urlLogger(HttpServletRequest request) {
        log.info("GET");
        log.info(request.getRequestURL().toString() + "?" + request.getQueryString());
        String responseQuery = request.getQueryString();

        boolean instAuthRequest = responseQuery != null && responseQuery.startsWith("code=");

        if (instAuthRequest)
        {
            String instAuthCode = StringUtils.substringAfter(responseQuery, "code=");
            log.info(responseQuery);
            log.info("instAuthCode: " + instAuthCode);

            String accessTokenURL = "https://api.instagram.com/oauth/access_token";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
            formData.add("app_id", "226365095211205");
            formData.add("app_secret", "9fa5d69f1ee9ddbfa1046326d66020bf");
            formData.add("grant_type", "authorization_code");
            formData.add("redirect_uri", "https://localhost:8080/");
            formData.add("code", instAuthCode);

            HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(formData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity( accessTokenURL, new HttpEntity<MultiValueMap<String, String>>(formData, headers) , String.class );

            log.info(response.getBody());
        }

        return "index";
    }
}
