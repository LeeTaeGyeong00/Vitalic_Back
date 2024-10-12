package com.example.vitalic_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VitalicBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(VitalicBackApplication.class, args);
    }

}
