package az.company.oauth2login.controller;

import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/promote")
    public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.promoteToAdmin(id));
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<String> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.disableUser(id));
    }
}