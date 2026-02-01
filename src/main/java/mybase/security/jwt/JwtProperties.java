package mybase.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "mybase-super-secret-jwt-key-minimum-256-bits-for-hs256-algorithm";

    private long expiration = 86400000; // 24 hours in milliseconds

    private long refreshExpiration = 604800000; // 7 days in milliseconds
}
