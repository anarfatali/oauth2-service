package az.company.oauth2login.service;

import az.company.oauth2login.domain.entity.User;
import az.company.oauth2login.domain.enums.RoleType;
import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.repository.RoleRepository;
import az.company.oauth2login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse promoteToAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + id
                ));

        roleRepository.findByName(RoleType.ROLE_ADMIN)
                .ifPresent(adminRole -> user.getRoles().add(adminRole));

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public String disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + id
                ));

        user.setEnabled(false);
        userRepository.save(user);

        return "User " + id + " has been disabled.";
    }
}