package az.company.oauth2login.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignOutRequest {

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}