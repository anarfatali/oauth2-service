package az.company.oauth2login.service;

import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + email
                ));
    }

    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }
}