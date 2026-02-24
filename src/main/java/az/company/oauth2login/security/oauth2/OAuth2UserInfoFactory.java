package az.company.oauth2login.security.oauth2;

import az.company.oauth2login.security.oauth2.discord.DiscordOAuth2UserInfo;
import az.company.oauth2login.security.oauth2.github.GithubOAuth2UserInfo;
import az.company.oauth2login.security.oauth2.google.GoogleOAuth2UserInfo;
import az.company.oauth2login.security.oauth2.instagram.InstagramOAuth2UserInfo;
import az.company.oauth2login.security.oauth2.linkedin.LinkedInOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId,
            Map<String, Object> attributes
    ) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "github" -> new GithubOAuth2UserInfo(attributes);
            case "instagram" -> new InstagramOAuth2UserInfo(attributes);
            case "linkedin" -> new LinkedInOAuth2UserInfo(attributes);
            case "discord" -> new DiscordOAuth2UserInfo(attributes);

            default -> throw new IllegalArgumentException(
                    "OAuth2 provider [" + registrationId + "] is not supported."
            );
        };
    }
}
