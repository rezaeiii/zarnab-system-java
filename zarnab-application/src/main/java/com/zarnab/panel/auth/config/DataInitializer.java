package com.zarnab.panel.auth.config;

import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.repository.IngotRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile(value = {"dev", "stage"})
public class DataInitializer {

    private final UserRepository userRepository;
    private final IngotRepository ingotRepository;

    @Bean
    @SneakyThrows
    CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Seeding database with sample users for development profile...");
                User adminUser = userRepository.save(User.builder()
                        .mobileNumber("09999999999")
                        .profileType(UserProfileType.NATURAL)
                        .enabled(true)
                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                                .firstName("آرمان")
                                .lastName("رضایی")
                                .nationalId("0000000000")
                                .build())
                        .roles(Set.of(Role.ADMIN, Role.USER, Role.REPRESENTATIVE))
                        .build());

                userRepository.save(User.builder()
                        .mobileNumber("09111111111")
                        .enabled(true)
                        .profileType(UserProfileType.NATURAL)
                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                                .firstName("شایان")
                                .lastName("رمضی")
                                .nationalId("0000000000")
                                .build())
                        .roles(Set.of(Role.USER, Role.COUNTER))
                        .build());
                log.info("Database seeding complete.");

            }
            User user = userRepository.findByMobileNumber("09999999999").orElseThrow();

            log.info("Seeding database with sample ingots...");
            boolean alreadyInit = !ingotRepository.findByOwnerId(user.getId()).isEmpty();
            if (!alreadyInit) {
                for (int i = 1; i <= 5; i++) {
                    ingotRepository.save(Ingot.builder()
                            .serial("ZRN-INGOT-00" + i)
                            .manufactureDate(LocalDate.now())
                            .karat(24)
                            .weightGrams(100.0 * i)
                            .owner(user)
                            .build());
                }
                log.info("Ingot seeding complete.");
            }

        };
    }
}
