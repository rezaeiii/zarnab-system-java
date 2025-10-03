package com.zarnab.panel.auth.service.sms;

import com.zarnab.panel.clients.sms.SmsServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * An adapter that "sends" an SMS by logging it to the console.
 * This implementation is only active when the 'dev' Spring profile is enabled.
 * It's a crucial tool for local development, allowing us to test the login flow
 * without incurring SMS costs or needing a real phone.
 */
@Slf4j
@Component
@Profile(value = {"dev"})
@RequiredArgsConstructor
public class ConsoleSmsService implements SmsService {

    private final SmsServiceClient smsServiceClient;

    @Override
    public void sendSms(String mobileNumber, String message) {
        smsServiceClient.send(mobileNumber, message)
                .doOnSuccess(response -> log.info("SMS sent successfully to {}: {}", mobileNumber, response.message()))
                .doOnError(e -> log.error("Failed to send SMS to {}: {}", mobileNumber, e.getMessage()))
                .subscribe();
    }
}
