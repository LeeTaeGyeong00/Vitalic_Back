package com.example.vitalic_back.controller;

import com.example.vitalic_back.dto.BudgetRequestDto;
import com.example.vitalic_back.dto.SignUpDto;

import com.example.vitalic_back.entity.User;
import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.repository.UserRepository;
import com.example.vitalic_back.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public Long signup(@Valid @RequestBody SignUpDto request) throws Exception {
        request.validate();
        return userService.SignUp(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signIn(@RequestBody Map<String, String> user) {
        // JWT 생성
        String token = userService.SignIn(user);

        // 응답 헤더에 JWT 추가
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    @GetMapping("/mypage")
    @ResponseStatus(HttpStatus.OK)
    public User getMyPage(Principal principal) {
        String email = principal.getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @PostMapping("/updateBudget")
    public ResponseEntity<String> updateBudget(HttpServletRequest request,
                                               @RequestBody BudgetRequestDto budgetRequest) {
        // JWT에서 email 가져오기
        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getUserPk(token);

        // 이메일로 userId 조회
        Long userId = userService.findUserIdByEmail(email);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // 예산 업데이트
        userService.updateUserBudget(userId, budgetRequest.getMonthlyBudget(), budgetRequest.getDailyBudget(), budgetRequest.getWeeklyBudget());

        return ResponseEntity.ok("User budget updated successfully");
    }
}
