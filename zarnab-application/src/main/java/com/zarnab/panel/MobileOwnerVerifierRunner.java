//package com.zarnab.panel;
//
//import com.zarnab.panel.clients.service.ShahkarInquiryClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import lombok.RequiredArgsConstructor;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MobileOwnerVerifierRunner implements CommandLineRunner {
//
//    private final ShahkarInquiryClient clientService; // the service where verifyMobileOwner exists
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Example test values
//        String nationalId = "0016914880";
//        String mobileNumber = "09102455281";
//
//        System.out.println("🔎 Verifying mobile owner...");
//
//        try {
//            Boolean matched = clientService.verifyMobileOwner(nationalId, mobileNumber)
//                    .doOnError(e -> log.error("Failed to deserialize UID response", e))
//                    .block();
//
//            System.out.println("✅ Verification result = " + matched);
//        } catch (Exception e) {
//            System.err.println("❌ Unexpected error: " + e.getMessage());
//        }
//    }
//}
