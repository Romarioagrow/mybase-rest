package mybase.security.oauth2;

import mybase.domain.types.AuthProvider;
import mybase.security.oauth2.provider.FacebookOAuth2UserInfo;
import mybase.security.oauth2.provider.GoogleOAuth2UserInfo;
import mybase.security.oauth2.provider.InstagramOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.name())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.name())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.INSTAGRAM.name())) {
            return new InstagramOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Login with " + registrationId + " is not supported yet.");
        }
    }
}
