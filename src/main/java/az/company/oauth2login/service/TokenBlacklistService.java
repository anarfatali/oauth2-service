package az.company.oauth2login.service;

import az.company.oauth2login.domain.entity.BlacklistedToken;
import az.company.oauth2login.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklist(String token, Instant expiryDate) {
        if (!blacklistedTokenRepository.existsByToken(token)) {
            BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                    .token(token)
                    .expiryDate(expiryDate)
                    .build();
            blacklistedTokenRepository.save(blacklistedToken);
        }
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    // Runs every hour — removes expired tokens that are no longer a threat anyway
    @Scheduled(fixedRate = 3600000)
    public void cleanUpExpiredTokens() {
        blacklistedTokenRepository.deleteAllExpired(Instant.now());
    }
}