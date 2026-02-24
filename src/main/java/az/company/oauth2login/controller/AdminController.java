package az.company.oauth2login.controller;

import az.company.oauth2login.domain.entity.User;
import az.company.oauth2login.domain.enums.RoleType;
import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.repository.RoleRepository;
import az.company.oauth2login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/users/{id}/promote")
    public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        roleRepository.findByName(RoleType.ROLE_ADMIN).ifPresent(adminRole ->
                user.getRoles().add(adminRole)
        );

        return ResponseEntity.ok(UserResponse.fromEntity(userRepository.save(user)));
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<String> disableUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        user.setEnabled(false);
        userRepository.save(user);

        return ResponseEntity.ok("User " + id + " has been disabled.");
    }
}