package mybase.security.oauth2.provider;

import mybase.security.oauth2.OAuth2UserInfo;

import java.util.Map;

public class InstagramOAuth2UserInfo extends OAuth2UserInfo {

    public InstagramOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getName() {
        return (String) attributes.get("username");
    }

    @Override
    public String getEmail() {
        // Instagram Basic Display API doesn't provide email
        // Email might be available through Instagram Graph API for business accounts
        return null;
    }

    @Override
    public String getImageUrl() {
        // Basic Display API doesn't provide profile picture directly
        // Would need additional API call to get profile picture
        return (String) attributes.get("profile_picture_url");
    }

    public String getUsername() {
        return (String) attributes.get("username");
    }

    public String getAccountType() {
        return (String) attributes.get("account_type");
    }
}
