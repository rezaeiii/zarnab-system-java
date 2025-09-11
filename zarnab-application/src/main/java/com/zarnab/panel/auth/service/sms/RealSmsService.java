package com.zarnab.panel.auth.service.sms;

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
@Profile("prod")
public class RealSmsService implements SmsService {

    @Override
    public void sendSms(String mobileNumber, String message) {
        // In a real application, you would add the integration logic here.
        // For example, using the Twilio SDK:
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(
        //      new com.twilio.type.PhoneNumber(mobileNumber),
        //      new com.twilio.type.PhoneNumber(TWILIO_NUMBER),
        //      message)
        // .create();

        log.error("PRODUCTION SMS PROVIDER IS NOT CONFIGURED. SMS for {} was not sent.", mobileNumber);
        log.info("[PRODUCTION SMS STUB] To: {}. Message: {}", mobileNumber, message);
    }
}
