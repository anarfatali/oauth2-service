package az.company.oauth2login.controller;

import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getProfile(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> getUserList() {
        return ResponseEntity.ok(
                "Total registered users: " + userService.getUserCount()
        );
    }
}