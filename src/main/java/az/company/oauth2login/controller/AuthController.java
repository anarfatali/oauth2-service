package az.company.oauth2login.controller;

import az.company.oauth2login.dto.request.LoginRequest;
import az.company.oauth2login.dto.request.RegisterRequest;
import az.company.oauth2login.dto.request.SignOutRequest;
import az.company.oauth2login.dto.response.AuthResponse;
import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signOut(@Valid @RequestBody SignOutRequest request) {
        authService.signOut(request.getAccessToken(), request.getRefreshToken());
        return ResponseEntity.ok("Signed out successfully. Tokens have been invalidated.");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }
}