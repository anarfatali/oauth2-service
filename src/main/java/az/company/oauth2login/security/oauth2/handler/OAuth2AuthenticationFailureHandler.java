package az.company.oauth2login.security.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import az.company.oauth2login.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        String message = exception.getMessage() != null
                ? exception.getMessage()
                : "OAuth2 authentication failed";

        log.error("OAuth2 authentication failed at {}: {}",
                request.getRequestURI(), message, exception);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        objectMapper.writeValue(response.getWriter(),
                ErrorResponse.builder()
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                        .error("OAuth2 Authentication Failed")
                        .message(message)
                        .path(request.getRequestURI())
                        .timestamp(ErrorResponse.now())
                        .build()
        );
    }
}