package com.zarnab.panel;

import com.zarnab.panel.clients.service.AddressInquiryClient;
import com.zarnab.panel.clients.service.PersonInquiryClient;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class ZarnabApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZarnabApplication.class, args);
    }

    @Bean
    @SneakyThrows
    CommandLineRunner runSampleJob(AddressInquiryClient addressInquiryClient, PersonInquiryClient personInquiryClient) {
        return args -> {
//            addressInquiryClient.getAddressInfo("1234567890").subscribe(System.out::println);
//            personInquiryClient.getPersonInfo("0016914880","1373/02/01").subscribe(System.out::println);
        };
    }
}
