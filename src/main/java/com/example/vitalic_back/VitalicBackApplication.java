package com.example.vitalic_back;

import com.example.vitalic_back.jwt.JwtTokenProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class VitalicBackApplication {

    public static void main(String[] args) {
        String encodedKey = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        String secretKey = new String(decodedKey);
        SpringApplication.run(VitalicBackApplication.class, args);
    }

}
