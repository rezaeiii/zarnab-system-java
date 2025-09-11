package com.zarnab.panel.auth.config;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile(value = {"dev", "docker"})
public class DataInitializer {

    private final UserRepository userRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Seeding database with sample users for development profile...");
                userRepository.save(User.builder()
                        .mobileNumber("09999999999")
                        .profileType(UserProfileType.NATURAL)
                        .enabled(true)
                        .roles(Set.of(Role.ADMIN, Role.USER, Role.REPRESENTATIVE))
                        .build());

                userRepository.save(User.builder()
                        .mobileNumber("09111111111")
                        .enabled(true)
                        .profileType(UserProfileType.NATURAL)
                        .roles(Set.of(Role.USER, Role.COUNTER))
                        .build());
                log.info("Database seeding complete.");
            }
        };
    }
}
