package com.zarnab.panel.auth.service.sms;

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
@Profile(value = {"dev", "docker"}) // This bean will only be created when the 'dev' profile is active
public class ConsoleSmsService implements SmsService {

    @Override
    public void sendSms(String mobileNumber, String message) {
        // In a real application, this would connect to an SMS gateway like Twilio.
        // For development, we simply print the message to the console.
        log.info("--- SIMULATED SMS ---");
        log.info("To: {}", mobileNumber);
        log.info("Message: {}", message);
        log.info("---------------------");
    }
}
