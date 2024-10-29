package com.example.vitalic_back.service;

import com.example.vitalic_back.dto.SignUpDto;
import com.example.vitalic_back.entity.User;

import java.util.Map;


public interface UserService {

    Long SignUp(SignUpDto signUpDto) throws Exception;

    String SignIn(Map<String, String> users);

    void createAdminAccount();
    // 소비량 비교 메서드 추가
    void compareUserBudget(Long dailyWithdraw, Long weeklyWithdraw, Long monthlyWithdraw, Long userId);

    void updateUserBudget(Long userId, Long monthlyBudget, Long dailyBudget, Long yearlyBudget);
    Long findUserIdByEmail(String userEmail);

    void compareUserBudgetWithDjango(Long userId);
    // findUserById 메서드 추가
    User findUserById(Long userId);

}
