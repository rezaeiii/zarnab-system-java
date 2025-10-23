package com.zarnab.panel.auth.config;

import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.model.ProductType;
import com.zarnab.panel.ingot.model.Purity;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.service.IngotService;
import com.zarnab.panel.ingot.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile(value = {"dev", "stage"})
public class DataInitializer {

    private final UserRepository userRepository;
    private final IngotRepository ingotRepository;
    private final TransferService transferService; // Injected TransferService

    @Bean
    @SneakyThrows
    CommandLineRunner initDatabase(IngotService ingotService) {
        return args -> {
//            User adminUser = null;
//            User counterUser = null;
//            User customerUser = null;
//
//            if (userRepository.count() == 0) {
//                log.info("Seeding database with sample users for development profile...");
//                adminUser = userRepository.save(User.builder()
//                        .mobileNumber("09999999999")
//                        .profileType(UserProfileType.NATURAL)
//                        .enabled(true)
//                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
//                                .firstName("ادمین")
//                                .lastName("سیستم")
//                                .nationalId("0000000000")
//                                .build())
//                        .roles(Set.of(Role.ADMIN, Role.REPRESENTATIVE))
//                        .build());
//
//                customerUser = userRepository.save(User.builder()
//                        .mobileNumber("09999999997")
//                        .enabled(true)
//                        .profileType(UserProfileType.NATURAL)
//                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
//                                .firstName("مشتری")
//                                .lastName("تستی")
//                                .nationalId("0000000001")
//                                .build())
//                        .roles(Set.of(Role.CUSTOMER))
//                        .build());
//
//                counterUser = userRepository.save(User.builder()
//                        .mobileNumber("09999999998")
//                        .enabled(true)
//                        .profileType(UserProfileType.NATURAL)
//                        .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
//                                .firstName("کانتر")
//                                .lastName("تستی")
//                                .nationalId("2222222222")
//                                .build())
//                        .roles(Set.of(Role.COUNTER))
//                        .build());
//                log.info("Database user seeding complete.");
//
//            }
//
//            boolean alreadyInit = ingotRepository.count() >= 100;
//            if (!alreadyInit) {
//                log.info("Seeding database with sample ingots and performing transfers...");
//
//                // Ensure users are loaded if not created in this run
//                if (adminUser == null) adminUser = userRepository.findByMobileNumber("09999999999").orElseThrow();
//                if (counterUser == null) counterUser = userRepository.findByMobileNumber("09999999998").orElseThrow();
//                if (customerUser == null) customerUser = userRepository.findByMobileNumber("09999999997").orElseThrow();
//
//                // 1. Create 100 Ingots
//                BatchCreateResponse batchResponse = ingotService.createBatch(new BatchCreateRequest(100, 1.0, Purity.P995, ProductType.INGOT_BAR, LocalDate.now()));
//                List<String> ingotSerials = batchResponse.serials();
//                log.info("Created {} ingots.", ingotSerials.size());
//
//                // 2. Transfer from Admin to Counter
//                log.info("Transferring ingots from Admin ({}) to Counter ({}).", adminUser.getMobileNumber(), counterUser.getMobileNumber());
//                transferService.verifyTransfer(adminUser, counterUser.getId(), ingotSerials);
//                log.info("Transfer to counter complete.");
//
//                // 3. Transfer from Counter to Customer
//                log.info("Transferring ingots from Counter ({}) to Customer ({}).", counterUser.getMobileNumber(), customerUser.getMobileNumber());
//                transferService.transferToCustomer(counterUser, customerUser.getId(), ingotSerials);
//                log.info("Transfer to customer complete.");
//
//                log.info("Ingot seeding and transfer flow complete.");
//            }
//
        };

    }
}
