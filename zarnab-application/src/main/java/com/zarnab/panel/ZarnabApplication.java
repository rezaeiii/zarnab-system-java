package com.zarnab.panel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class ZarnabApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZarnabApplication.class, args);
    }

}
