package com.zarnab.panel.auth.config;

import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.model.ProductType;
import com.zarnab.panel.ingot.model.Purity;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.service.IngotService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;

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
    CommandLineRunner initDatabase(IngotService ingotService) {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Seeding database with sample users for development profile...");
                User adminUser = userRepository.save(User.builder()
                        .mobileNumber("09999999999")
                        .profileType(UserProfileType.NATURAL)
                        .enabled(true)
                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                                .firstName("آرمان")
                                .lastName("حسینی")
                                .nationalId("0000000000")
                                .build())
                        .roles(Set.of(Role.ADMIN, Role.CUSTOMER, Role.REPRESENTATIVE))
                        .build());

                User adminUser2 = userRepository.save(User.builder()
                        .mobileNumber("09102455281")
                        .profileType(UserProfileType.NATURAL)
                        .enabled(true)
                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                                .firstName("آرمان")
                                .lastName("حسینی")
                                .nationalId("1111111111")
                                .build())
                        .roles(Set.of(Role.ADMIN, Role.CUSTOMER, Role.REPRESENTATIVE))
                        .build());

                userRepository.save(User.builder()
                        .mobileNumber("09999999997")
                        .enabled(true)
                        .profileType(UserProfileType.NATURAL)
                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                                .firstName("مشتری")
                                .lastName("رمضی")
                                .nationalId("0000000001")
                                .build())
                        .roles(Set.of(Role.CUSTOMER))
                        .build());

                userRepository.save(User.builder()
                        .mobileNumber("09999999998")
                        .enabled(true)
                        .profileType(UserProfileType.NATURAL)
                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                                .firstName("کانتر")
                                .lastName("رمضی")
                                .nationalId("2222222222")
                                .build())
                        .roles(Set.of(Role.COUNTER))
                        .build());
                log.info("Database seeding complete.");

            }

//            User user = userRepository.findByMobileNumber("09999999999").orElseThrow();
            boolean alreadyInit = ingotRepository.findAll(Pageable.ofSize(15)).getSize() > 14;
            if (!alreadyInit) {
                log.info("Seeding database with sample ingots...");
                ingotService.createBatch(new BatchCreateRequest(10, 0.1, Purity.P995, ProductType.COIN_FULL, LocalDate.now()));
                log.info("Ingot seeding complete.");
            }

        };
    }
}
