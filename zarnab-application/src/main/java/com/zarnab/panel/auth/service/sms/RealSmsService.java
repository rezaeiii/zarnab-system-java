package com.zarnab.panel.auth.service.sms;

import com.zarnab.panel.clients.sms.SmsServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A production implementation of the SmsPort.
 * This class is a placeholder and should be integrated with a real SMS gateway provider
 * like Twilio, Vonage, etc. It is only active in the "prod" profile.
 */
@Slf4j
@Component
@Profile(value = {"stage"})
@RequiredArgsConstructor
public class RealSmsService implements SmsService {

    private final SmsServiceClient smsServiceClient;

    @Override
    public void sendSms(String mobileNumber, String message) {
        smsServiceClient.send(mobileNumber, message)
                .doOnSuccess(response -> log.info("SMS sent successfully to {}: {}", mobileNumber, response.message()))
                .doOnError(e -> log.error("Failed to send SMS to {}: {}", mobileNumber, e.getMessage()))
                .subscribe();
    }
}
