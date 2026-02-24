package az.company.oauth2login.security.oauth2;

import az.company.oauth2login.domain.entity.User;
import az.company.oauth2login.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (response.isCommitted()) {
            log.warn("Response already committed — cannot write.");
            return;
        }

        CustomOAuth2UserPrincipal principal = (CustomOAuth2UserPrincipal) authentication.getPrincipal();

        User user = principal.getUser();

        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = tokenProvider.generateAccessTokenFromEmail(user.getEmail(), roles);
        String refreshToken = tokenProvider.generateRefreshTokenFromEmail(user.getEmail(), roles);

        log.info("OAuth2 login successful for: {}", user.getEmail());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        objectMapper.writeValue(response.getWriter(), Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "tokenType", "Bearer",
                "email", user.getEmail(),
                "name", user.getName(),
                "roles", roles
        ));
    }
}