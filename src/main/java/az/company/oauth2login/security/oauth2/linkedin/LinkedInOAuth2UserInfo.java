package az.company.oauth2login.security.oauth2.linkedin;

import az.company.oauth2login.security.oauth2.OAuth2UserInfo;

import java.util.Map;

public class LinkedInOAuth2UserInfo extends OAuth2UserInfo {

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }

    public LinkedInOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getImageUrl() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }
}
