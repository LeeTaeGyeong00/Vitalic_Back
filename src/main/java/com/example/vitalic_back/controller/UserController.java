package com.example.vitalic_back.controller;

import com.example.vitalic_back.batch.FirstSchedule;
import com.example.vitalic_back.dto.BudgetRequestDto;
import com.example.vitalic_back.dto.SignUpDto;

import com.example.vitalic_back.entity.User;
import com.example.vitalic_back.entity.UserToken;
import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.jwt.JwtTokenStorage;
import com.example.vitalic_back.repository.UserRepository;
import com.example.vitalic_back.repository.UserTokenRepository;
import com.example.vitalic_back.service.CustomUserDetails;
import com.example.vitalic_back.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//@RequiredArgsConstructor
//@RequestMapping("/user")
//@RestController
//@Slf4j
//public class UserController {
//    private final UserService userService;
//    private final UserRepository userRepository;
//    private final JwtTokenProvider jwtTokenProvider;
//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
//
//    @PostMapping("/signup")
//    @ResponseStatus(HttpStatus.OK)
//    public Long signup(@Valid @RequestBody SignUpDto request) throws Exception {
//        request.validate();
//        return userService.SignUp(request);
//    }
//
//    @PostMapping("/signin")
//    public ResponseEntity<Void> signIn(@RequestBody Map<String, String> user) {
//        // JWT 생성
//        String token = userService.SignIn(user);
//
//        // 응답 헤더에 JWT 추가
//        return ResponseEntity.ok()
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .build();
//    }
//
//    @GetMapping("/mypage")
//    @ResponseStatus(HttpStatus.OK)
//    public User getMyPage(Principal principal) {
//        String email = principal.getName();
//        return userRepository.findByUserEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//    }
//
//    @PostMapping("/updateBudget")
//    public ResponseEntity<String> updateBudget(HttpServletRequest request,
//                                               @RequestBody BudgetRequestDto budgetRequest) {
//        // JWT에서 email 가져오기
//        String token = jwtTokenProvider.resolveToken(request);
//        String email = jwtTokenProvider.getUserPk(token);
//
//        // 이메일로 userId 조회
//        Long userId = userService.findUserIdByEmail(email);
//        if (userId == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//
//        // 예산 업데이트
//        userService.updateUserBudget(userId, budgetRequest.getMonthlyBudget(), budgetRequest.getDailyBudget(), budgetRequest.getWeeklyBudget());
//
//        return ResponseEntity.ok("User budget updated successfully");
//    }
//}
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    private final UserTokenRepository userTokenRepository;

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FirstSchedule firstSchedule;
    private final JwtTokenStorage jwtTokenStorage;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody SignUpDto request) throws Exception {
        request.validate();
        Long userId = userService.SignUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody Map<String, String> user) throws Exception {
        String token = userService.SignIn(user);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        Long userId = userService.findUserIdByEmail(user.get("userEmail"));

        // 인메모리에 토큰 저장
        jwtTokenStorage.storeToken(userId, token);

        // 사용자 인증 정보 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(response);
    }

    @GetMapping("/mypage")
    public ResponseEntity<User> getMyPage(Principal principal) {
        String email = principal.getName();
        System.out.println(email);
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/updateBudget")
    public ResponseEntity<String> updateBudget(HttpServletRequest request,
                                               @RequestBody BudgetRequestDto budgetRequest) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = jwtTokenProvider.getUserEmailFromJWT(token);
        Long userId = userService.findUserIdByEmail(email);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userService.updateUserBudget(userId, budgetRequest.getMonthlyBudget(), budgetRequest.getDailyBudget(), budgetRequest.getWeeklyBudget());
        return ResponseEntity.ok("User budget updated successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        if (authentication != null) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            Long userId = user.getUserId();

            // 인메모리에 저장된 토큰 삭제
            jwtTokenStorage.removeToken(userId);

            // SecurityContext에서 인증 정보 제거
            SecurityContextHolder.clearContext();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
}