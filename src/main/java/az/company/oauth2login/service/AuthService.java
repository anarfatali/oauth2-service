package az.company.oauth2login.service;

import az.company.oauth2login.domain.entity.Role;
import az.company.oauth2login.domain.entity.User;
import az.company.oauth2login.domain.enums.AuthProvider;
import az.company.oauth2login.domain.enums.RoleType;
import az.company.oauth2login.dto.request.LoginRequest;
import az.company.oauth2login.dto.request.RegisterRequest;
import az.company.oauth2login.dto.response.AuthResponse;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


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

    // ----------------------------------------------------------------
    // Sign Out
    // Note: JWT is stateless — true invalidation requires a token
    // blacklist or short expiry. We handle the client-side contract
    // here and will add blacklisting in a later step if needed.
    // ----------------------------------------------------------------

    public void signOut() {
        // Clears the security context for the current thread.
        // The client must delete the token on their side.
        SecurityContextHolder.clearContext();
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
                .name(userDetails.getUsername())
                .roles(roles)
                .build();
    }
}