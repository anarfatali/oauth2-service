package az.company.oauth2login.config;

import az.company.oauth2login.domain.entity.Role;
import az.company.oauth2login.domain.enums.RoleType;
import az.company.oauth2login.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
    }

    private void seedRoles() {
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (roleRepository.findByName(roleType).isEmpty()) {
                roleRepository.save(
                        Role.builder()
                                .name(roleType)
                                .build()
                );
                log.info("Seeded role: {}", roleType);
            }
        });

        log.info("Role seeding complete. Total roles: {}", roleRepository.count());
    }
}