package az.company.oauth2login.security.oauth2;

import az.company.oauth2login.domain.entity.Role;
import az.company.oauth2login.domain.entity.User;
import az.company.oauth2login.domain.enums.AuthProvider;
import az.company.oauth2login.domain.enums.RoleType;
import az.company.oauth2login.repository.RoleRepository;
import az.company.oauth2login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        validateUserInfo(userInfo);

        User user = processOAuth2User(userInfo, registrationId);

        return new CustomOAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }

    private User processOAuth2User(OAuth2UserInfo userInfo, String registrationId) {
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // if user registered locally with this email — block OAuth2 login
            if (!user.getProvider().equals(provider)) {
                throw new OAuth2AuthenticationException(
                        "This email is already registered with " + user.getProvider() +
                                ". Please log in using your " + user.getProvider() + " account."
                );
            }

            return updateExistingUser(user, userInfo);

        } else {
            return registerNewOAuth2User(userInfo, provider);
        }
    }

    private User registerNewOAuth2User(OAuth2UserInfo userInfo, AuthProvider provider) {
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));

        User newUser = User.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .imageUrl(userInfo.getImageUrl())
                .provider(provider)
                .providerId(userInfo.getId())
                .password(null)              // OAuth2 users have no password
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        log.info("Registering new OAuth2 user: {}", userInfo.getEmail());
        return userRepository.save(newUser);
    }

    private User updateExistingUser(User user, OAuth2UserInfo userInfo) {
        user.setName(userInfo.getName());
        user.setImageUrl(userInfo.getImageUrl());
        return userRepository.save(user);
    }

    private void validateUserInfo(OAuth2UserInfo userInfo) {
        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(
                    "Email not returned from OAuth2 provider. " +
                            "Ensure the 'email' scope is requested."
            );
        }
    }
}