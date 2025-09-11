//package com.zarnab.panel.auth.service.otp;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.zarnab.panel.auth.service.out.OtpPort;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
///**
// * An adapter that stores OTPs in an in-memory cache.
// * This implementation is only active when the 'dev' Spring profile is enabled.
// * It's suitable for local development as it requires no external services.
// * The cache automatically evicts entries after a set time.
// */
//@Component
//@Profile("dev")
//public class InMemoryOtpAdapter implements OtpPort {
//
//    private final Cache<String, String> otpCache;
//
//    public InMemoryOtpAdapter() {
//        this.otpCache = CacheBuilder.newBuilder()
//                .expireAfterWrite(5, TimeUnit.MINUTES) // OTPs are valid for 5 minutes
//                .maximumSize(1000) // Store up to 1000 OTPs at a time
//                .build();
//    }
//
//    @Override
//    public void saveOtp(String mobileNumber, String otp) {
//        otpCache.put(mobileNumber, otp);
//    }
//
//    @Override
//    public Optional<String> getOtp(String mobileNumber) {
//        return Optional.ofNullable(otpCache.getIfPresent(mobileNumber));
//    }
//
//    @Override
//    public void clearOtp(String mobileNumber) {
//        otpCache.invalidate(mobileNumber);
//    }
//}
//
