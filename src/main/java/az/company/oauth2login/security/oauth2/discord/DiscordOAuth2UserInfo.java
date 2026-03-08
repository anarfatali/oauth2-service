package az.company.oauth2login.security.oauth2.discord;

import az.company.oauth2login.security.oauth2.OAuth2UserInfo;

import java.util.Map;

public class DiscordOAuth2UserInfo extends OAuth2UserInfo {

    public DiscordOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        // "global_name" is the display name; fallback to "username"
        String globalName = (String) attributes.get("global_name");
        return globalName != null ? globalName : (String) attributes.get("username");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        String id = (String) attributes.get("id");
        String avatarHash = (String) attributes.get("avatar");

        if (avatarHash != null) {
            return String.format("https://cdn.discordapp.com/avatars/%s/%s.png", id, avatarHash);
        }
        // Default Discord avatar
        return "https://cdn.discordapp.com/embed/avatars/0.png";
    }
}