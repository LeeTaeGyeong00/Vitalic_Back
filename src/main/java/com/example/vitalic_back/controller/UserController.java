package com.example.vitalic_back.controller;

import com.example.vitalic_back.DTO.SignUpDto;
import com.example.vitalic_back.entity.User;
import com.example.vitalic_back.repository.UserRepository;
import com.example.vitalic_back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public Long signup(@Valid @RequestBody SignUpDto request) throws Exception {
        request.validate();
        return userService.SignUp(request);
    }

    @PostMapping("/signin")
    public String signIn(@RequestBody Map<String, String> user){
        return userService.SignIn(user);
    }

    @GetMapping("/mypage")
    @ResponseStatus(HttpStatus.OK)
    public User getMyPage(Principal principal) {
        String email = principal.getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
