package mybase.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.config.AppProperties;
import mybase.security.UserPrincipal;
import mybase.security.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Long userId;
        String userEmail;

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            userId = userPrincipal.getId();
            userEmail = userPrincipal.getEmail();
        } else if (principal instanceof OidcUser) {
            // This shouldn't happen if CustomOidcUserService is working correctly,
            // but handle it as a fallback
            OidcUser oidcUser = (OidcUser) principal;
            log.warn("Received OidcUser instead of UserPrincipal. Email: {}", oidcUser.getEmail());
            // We can't generate a proper JWT without the user ID from our database
            // Redirect with error
            String redirectUri = appProperties.getOauth2().getAuthorizedRedirectUri();
            return UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "Authentication processing error. Please try again.")
                    .build()
                    .toUriString();
        } else if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            log.warn("Received OAuth2User instead of UserPrincipal. Attributes: {}", oauth2User.getAttributes());
            String redirectUri = appProperties.getOauth2().getAuthorizedRedirectUri();
            return UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "Authentication processing error. Please try again.")
                    .build()
                    .toUriString();
        } else {
            log.error("Unknown principal type: {}", principal.getClass().getName());
            String redirectUri = appProperties.getOauth2().getAuthorizedRedirectUri();
            return UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "Unknown authentication type")
                    .build()
                    .toUriString();
        }

        String token = tokenProvider.generateToken(userId);
        String refreshToken = tokenProvider.generateRefreshToken(userId);

        String redirectUri = appProperties.getOauth2().getAuthorizedRedirectUri();

        log.info("OAuth2 authentication successful for user: {}. Redirecting to: {}",
                userEmail, redirectUri);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
    }
}
