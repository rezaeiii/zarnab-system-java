//package com.zarnab.panel;
//
//import com.zarnab.panel.clients.service.ShahkarInquiryClient;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import lombok.RequiredArgsConstructor;
//import reactor.core.publisher.Mono;
//
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
//                    .doOnError(e -> System.err.println("Error during call: " + e.getMessage()))
//                    .onErrorReturn(false)
//                    .block();
////            Boolean result = clientService.verifyMobileOwner(nationalId, mobileNumber)
////                                          .onErrorResume(ex -> {
////                                              System.err.println("❌ API call failed: " + ex.getMessage());
////                                              return Mono.just(false);
////                                          })
////                                          .block(); // block only here for demo/testing
//
//            System.out.println("✅ Verification result = " + matched);
//        } catch (Exception e) {
//            System.err.println("❌ Unexpected error: " + e.getMessage());
//        }
//    }
//}
