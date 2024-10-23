package com.example.vitalic_back.service;

import com.example.vitalic_back.dto.SignUpDto;
import com.example.vitalic_back.entity.Role;
import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.repository.UserRepository;
import com.example.vitalic_back.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;
    private final RestTemplate restTemplate;

    @Transactional
    @Override
    public Long SignUp(SignUpDto requestDto) throws Exception {
        if (userRepository.findByUserEmail(requestDto.getUserEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다");
        }
        requestDto.validate(); // 비밀번호 검증

        // 엔티티 생성 후 비밀번호 인코딩
        User user = requestDto.toEntity();
        user.encodePassword(passwordEncoder); // 비밀번호 인코딩

        // 인코딩된 비밀번호로 저장
        user = userRepository.save(user);
        user.addUserAuthority();
        return user.getUserNo();
    }

    @Override
    public String SignIn(Map<String, String> users) {
        User user = userRepository.findByUserEmail(users.get("userEmail"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 email 입니다"));

        String password = users.get("userPw");
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().name());

        return jwtTokenProvider.createToken(user.getUsername(), roles);
    }

    @Override
    public void createAdminAccount() {
        if (!userRepository.existsByUserEmail("admin@example.com")) {
            User admin = User.builder()
                    .userEmail("admin@example.com")
                    .userPw(passwordEncoder.encode("admin123"))
                    .userName("Admin")
                    .userPH("010-1234-5678")
                    .regDate(LocalDate.now())
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }

    @Override
    public void compareUserBudget(Long dailyWithdraw, Long weeklyWithdraw, Long monthlyWithdraw, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        // 유저의 예산 값 가져오기 (null 체크)
        Long userBudgetDay = user.getUserBudgetDay() != null ? user.getUserBudgetDay() : 0L;
        Long userBudgetWeek = user.getUserBudgetWeek() != null ? user.getUserBudgetWeek() : 0L;
        Long userBudgetMonth = user.getUserBudgetMonth() != null ? user.getUserBudgetMonth() : 0L;

        if (dailyWithdraw > user.getUserBudgetDay()) {
            sendOverBudgetEmail(user.getUserEmail(), "일간", dailyWithdraw);
        }
        if (weeklyWithdraw > user.getUserBudgetWeek()) {
            sendOverBudgetEmail(user.getUserEmail(), "주간", weeklyWithdraw);
        }
        if (monthlyWithdraw > user.getUserBudgetMonth()) {
            sendOverBudgetEmail(user.getUserEmail(), "월간", monthlyWithdraw);
        }
    }

    private void sendOverBudgetEmail(String email, String budgetType, Long withdrawTotal) {
        String subject = budgetType + " 소비량 초과 알림";
        String text = "당신의 " + budgetType + " 소비량이 설정한 기준을 초과했습니다. 소비량: " + withdrawTotal;
        mailService.sendEmail(email, subject, text);
    }

    @Transactional
    @Override
    public void updateUserBudget(Long userId, Long monthlyBudget, Long dailyBudget, Long yearlyBudget) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 예산 업데이트
        user.setUserBudgetMonth(monthlyBudget);
        user.setUserBudgetDay(dailyBudget);
        user.setUserBudgetWeek(yearlyBudget);

        userRepository.save(user); // 변경사항 저장
    }

    @Override
    public Long findUserIdByEmail(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.getUserNo();
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public void compareUserBudgetWithDjango(Long userId) {
        // Django API 요청
        String djangoApiUrl = "http://127.0.0.1:8000/api/report/mwd";
        ResponseEntity<Map> response = restTemplate.postForEntity(djangoApiUrl, null, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) return;

        // JSON 파싱
        Map<String, Object> dailySummary = (Map<String, Object>) responseBody.get("daily_summary");
        Map<String, Object> weeklySummary = (Map<String, Object>) responseBody.get("weekly_summary");
        Map<String, Object> monthlySummary = (Map<String, Object>) responseBody.get("monthly_summary");

        Long dailyWithdraw = Long.parseLong(dailySummary.get("withdraw_total").toString());
        //Long dailyWithdraw = 10000L; // 임시 일일 출금 총액
        Long weeklyWithdraw = Long.parseLong(weeklySummary.get("withdraw_total").toString());
        Long monthlyWithdraw = Long.parseLong(monthlySummary.get("withdraw_total").toString());

        // 회원 예산 정보 가져오기
        User user = findUserById(userId); // UserServiceImpl에서 findUserById 사용

        // 예산 확인 및 출력
        Long userBudgetDay = user.getUserBudgetDay(); // 유저 예산 값 가져오기

        // 값 확인을 위한 출력
        System.out.println("User ID: " + userId);
        System.out.println("Daily Withdraw Total: " + dailyWithdraw);
        System.out.println("Weekly Withdraw Total: " + weeklyWithdraw);
        System.out.println("Monthly Withdraw Total: " + monthlyWithdraw);
        System.out.println("User Budget for the Day: " + userBudgetDay); // 유저 예산 출력

        // 예산 초과 여부 확인
        compareUserBudget(dailyWithdraw, weeklyWithdraw, monthlyWithdraw, userId);
    }
}
