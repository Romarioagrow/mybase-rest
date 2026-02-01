package mybase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private OAuth2 oauth2 = new OAuth2();
    private Cors cors = new Cors();

    @Data
    public static class OAuth2 {
        private String authorizedRedirectUri = "http://localhost:3000/oauth2/redirect";
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:9000");
    }
}
