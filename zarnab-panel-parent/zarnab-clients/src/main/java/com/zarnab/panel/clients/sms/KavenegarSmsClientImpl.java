//package com.zarnab.panel.clients.sms;
//
//import com.kavenegar.sdk.KavenegarApi;
//import com.kavenegar.sdk.excepctions.ApiException;
//import com.kavenegar.sdk.excepctions.HttpException;
//import com.kavenegar.sdk.models.SendResult;
//import com.zarnab.panel.clients.config.ClientsConfig;
//import com.zarnab.panel.clients.sms.dto.SmsSendData;
//import com.zarnab.panel.clients.sms.dto.SmsSendRequest;
//import com.zarnab.panel.clients.sms.dto.SmsSendResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Slf4j
//@Service
//@Primary
//@ConditionalOnProperty(name = "api.clients.sms.provider", havingValue = "kavenegar")
//public class KavenegarSmsClientImpl implements SmsServiceClient {
//
//    private final ClientsConfig.Kavenegar config;
//
//    public KavenegarSmsClientImpl(ClientsConfig properties) {
//        this.config = properties.kavenegar();
//    }
//
//    @Override
//    public Mono<SmsSendResponse> send(SmsSendRequest request) {
//        return send(request.mobiles().get(0), request.messageTexts().get(0));
//    }
//
//    @Override
//    public Mono<SmsSendResponse> send(String mobile, String message) {
//
//        try {
//            KavenegarApi api = new KavenegarApi(config.apiKey());
//            SendResult result = api.send(String.valueOf(config.lineNumber()), mobile, message);
//            return Mono.just(new SmsSendResponse(new SmsSendData(result.getMessage(), List.of(result.getMessageId())), result.getStatusText(), result.getMessage()));
//        } catch (HttpException ex) {
//            System.out.print("HttpException  : " + ex.getMessage());
//        } catch (ApiException ex) {
//            System.out.print("ApiException : " + ex.getMessage());
//        }
//        return Mono.empty();
//    }
//
//}
