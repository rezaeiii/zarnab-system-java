package com.zarnab.panel.clients.sms;

import com.zarnab.panel.clients.config.ClientsConfig;
import com.zarnab.panel.clients.sms.dto.MeliPayamakSendResponseDto;
import com.zarnab.panel.clients.sms.dto.SmsSendData;
import com.zarnab.panel.clients.sms.dto.SmsSendRequest;
import com.zarnab.panel.clients.sms.dto.SmsSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Service
@Primary
@ConditionalOnProperty(name = "api.clients.sms.provider", havingValue = "melipayamak")
public class MeliPayamakSmsClientImpl implements SmsServiceClient {

    private final WebClient meliPayamakWebClient;
    private final ClientsConfig.MeliPayamakSmsConfig config;

    public MeliPayamakSmsClientImpl(@Qualifier("meliPayamakWebClient") WebClient meliPayamakWebClient,
                                    ClientsConfig properties) {
        this.meliPayamakWebClient = meliPayamakWebClient;
        this.config = properties.meliPayamak();
    }

    @Override
    public Mono<SmsSendResponse> send(SmsSendRequest request) {
        return send(request.mobiles().get(0), request.messageTexts().get(0));
    }

    @Override
    public Mono<SmsSendResponse> send(String mobile, String message) {
        return meliPayamakWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/SendSMS/SendSMS")
                        .queryParam("username", config.username())
                        .queryParam("password", config.password())
                        .queryParam("to", mobile)
                        .queryParam("from", config.from())
                        .queryParam("text", message)
                        .queryParam("isflash", false)
                        .build())
                .retrieve()
                .bodyToMono(MeliPayamakSendResponseDto.class)
                .flatMap(this::handleResponse);
    }

    private Mono<SmsSendResponse> handleResponse(MeliPayamakSendResponseDto response) {
        if (response.getRetStatus() == 1) { // Success
            SmsSendData sendData = new SmsSendData(response.getValue(), Collections.emptyList());
            return Mono.just(new SmsSendResponse(sendData, "SUCCESS", response.getStrRetStatus()));
        } else {
            // Error
            String errorMessage = MeliPayamakException.getErrorMessage(response.getRetStatus());
            log.error("MeliPayamak API error. Status: {}, Message: {}", response.getRetStatus(), errorMessage);
            return Mono.error(new MeliPayamakException(errorMessage));
        }
    }

    // Custom exception for MeliPayamak specific errors
    public static class MeliPayamakException extends RuntimeException {
        public MeliPayamakException(String message) {
            super(message);
        }

        public static String getErrorMessage(int code) {
            return switch (code) {
                case 0 -> "نام کاربری یا رمز عبور اشتباه می باشد.";
                case 2 -> "اعتبار کافی نمی باشد.";
                case 3 -> "محدودیت در ارسال روزانه";
                case 4 -> "محدودیت در حجم ارسال";
                case 5 -> "شماره فرستنده معتبر نمی باشد.";
                case 6 -> "سامانه در حال بروزرسانی می باشد.";
                case 7 -> "متن حاوی کلمه فیلتر شده می باشد.";
                case 9 -> "ارسال از خطوط عمومی از طریق وب سرویس امکان پذیر نمی باشد.";
                case 10 -> "کاربر مورد نظر فعال نمی باشد.";
                case 11 -> "ارسال نشده";
                case 12 -> "مدارک کاربر کامل نمی باشد.";
                case 14 -> "متن حاوی لینک می باشد.";
                case 15 -> "ارسال به بیش از 1 شماره همراه بدون درج 'لغو11' ممکن نیست.";
                case 16 -> "شماره گیرنده ای یافت نشد";
                case 17 -> "متن پیامک خالی می باشد";
                case 18 -> "شماره گیرنده نامعتبر است";
                case 35 -> "در REST به معنای وجود شماره در لیست سیاه مخابرات می‌باشد.";
                case 108 -> "مسدود شدن IP به دلیل تالش ناموفق استفاده ازAPI";
                case 109 -> "الزام تنظیم IP مجاز برای استفاده از API";
                case 110 -> "الزام استفاده از ApiKey به جای رمز عبور";
                default -> "An unknown error occurred with MeliPayamak API. Status code: " + code;
            };
        }
    }
}
