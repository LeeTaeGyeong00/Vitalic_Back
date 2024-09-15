package com.example.vitalic_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class VitalicBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(VitalicBackApplication.class, args);
    }

}
