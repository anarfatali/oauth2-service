package az.company.oauth2login.security.oauth2.github;

import az.company.oauth2login.security.oauth2.OAuth2UserInfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
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
    public String getEmail() {
        return "";
    }

    @Override
    public String getImageUrl() {
        return "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }
}
