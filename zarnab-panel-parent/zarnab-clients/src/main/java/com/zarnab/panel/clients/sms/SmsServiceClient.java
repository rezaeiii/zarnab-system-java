package com.zarnab.panel.clients.sms;

import com.zarnab.panel.clients.sms.dto.SmsSendRequest;
import com.zarnab.panel.clients.sms.dto.SmsSendResponse;
import reactor.core.publisher.Mono;

public interface SmsServiceClient {
    Mono<SmsSendResponse> send(SmsSendRequest request);

    Mono<SmsSendResponse> send(String mobile, String message);
}
