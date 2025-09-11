package com.zarnab.panel.auth.service.sms;

public interface SmsService {

    void sendSms(String mobileNumber, String message);

}