package com.casaflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CasaFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(CasaFlowApplication.class, args);
    }
}
