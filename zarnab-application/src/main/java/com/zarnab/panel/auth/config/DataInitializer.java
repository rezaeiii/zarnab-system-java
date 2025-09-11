package com.zarnab.panel.auth.config;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Seeding database with sample users for development profile...");
                userRepository.save(User.builder()
                        .mobileNumber("+11111111111")
                        .enabled(true)
                        .roles(Set.of(Role.ADMIN, Role.USER, Role.REPRESENTATIVE))
                        .build());

                userRepository.save(User.builder()
                        .mobileNumber("+22222222222")
                        .enabled(true)
                        .roles(Set.of(Role.USER, Role.COUNTER))
                        .build());
                log.info("Database seeding complete.");
            }
        };
    }
}
