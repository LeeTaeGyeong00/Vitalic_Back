package com.example.vitalic_back;
import com.example.vitalic_back.entity.Role;
import com.example.vitalic_back.repository.UserRepository;
import com.example.vitalic_back.service.UserService;
import com.example.vitalic_back.service.UserServiceImpl;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class AdminAccountInitializer {

    private final UserService userService;  // 인터페이스 사용

    public AdminAccountInitializer(UserService userService) {  // 대소문자 수정
        this.userService = userService;
    }

    @Bean
    public CommandLineRunner initAdminAccount() {
        return args -> {
            userService.createAdminAccount();  // Admin 계정 생성
        };
    }
}