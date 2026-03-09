package az.company.oauth2login.service;

import az.company.oauth2login.domain.entity.Role;
import az.company.oauth2login.domain.entity.User;
import az.company.oauth2login.domain.enums.AuthProvider;
import az.company.oauth2login.domain.enums.RoleType;
import az.company.oauth2login.dto.request.LoginRequest;
import az.company.oauth2login.dto.request.RegisterRequest;
import az.company.oauth2login.dto.response.AuthResponse;
import az.company.oauth2login.dto.response.UserResponse;
import az.company.oauth2login.repository.RoleRepository;
import az.company.oauth2login.repository.UserRepository;
import az.company.oauth2login.security.UserDetailsImpl;
import az.company.oauth2login.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists: " + request.getEmail()
            );
        }

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException(
                        "Default role ROLE_USER not found. Roles table isn't initialized."
                ));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider(AuthProvider.LOCAL)
                .roles(Set.of(userRole))
                .enabled(true)
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return buildAuthResponse(authentication);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return buildAuthResponse(authentication);
    }

    @Transactional
    public void signOut(String accessToken, String refreshToken) {
        Instant accessExpiry = tokenProvider.getExpiryDateFromToken(accessToken);
        tokenBlacklistService.blacklist(accessToken, accessExpiry);

        Instant refreshExpiry = tokenProvider.getExpiryDateFromToken(refreshToken);
        tokenBlacklistService.blacklist(refreshToken, refreshExpiry);

        SecurityContextHolder.clearContext();
    }

    public UserResponse getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + email
                ));
    }

    private AuthResponse buildAuthResponse(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(userDetails.getEmail())
                .name(userDetails.getName())
                .roles(roles)
                .build();
    }
}