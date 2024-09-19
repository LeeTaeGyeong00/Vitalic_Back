package com.example.vitalic_back.Service;

import com.example.vitalic_back.DTO.SignUpDto;

import java.util.Map;

public interface UserService {

    public Long SignUp(SignUpDto signUpDto) throws Exception;

    public String SignIn(Map<String, String> users);
}
