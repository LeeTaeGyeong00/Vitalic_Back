package com.example.vitalic_back.batch;

import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.jwt.JwtTokenStorage;
import com.example.vitalic_back.service.UserService;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@Configuration
public class FirstSchedule {
    private final FirstBatch firstBatch;
    private final JwtTokenStorage jwtTokenStorage;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    public FirstSchedule(FirstBatch firstBatch, JwtTokenStorage jwtTokenStorage, UserService userService, JwtTokenProvider jwtTokenProvider, JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.firstBatch = firstBatch;
        this.jwtTokenStorage = jwtTokenStorage;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }
@Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
public void runFirstJob() throws Exception {
    System.out.println("First schedule start");

    if (!isJobRunning("firstJob")) { // Check if the job is already running
        // 현재 활성화된 사용자 ID를 가져옵니다
        Long userId = jwtTokenStorage.getActiveUserId(); // 가장 최근 로그인된 사용자의 ID 가져오기

        // 인메모리에 저장된 토큰 가져오기
        String token = jwtTokenStorage.getToken(userId); // 특정 사용자 ID로 JWT 토큰 조회

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 유효한 토큰이 있을 때 작업 실행
            launchJob(token);
        } else {
            System.out.println("Invalid or missing token.");
        }
    } else {
        System.out.println("Job is already running. Skipping this execution.");
    }
}
    private Long getCurrentUserId() {
        // Spring Security를 사용하여 현재 사용자 ID를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 사용자 ID를 가져오는 로직 (예: Principal에서 가져오기)
            // 예: return ((CustomUserDetails) authentication.getPrincipal()).getId();

            // 더미 ID 반환 (테스트용)
            return 3L; // 실제로는 인증된 사용자의 ID를 반환해야 함
        }
        throw new IllegalStateException("User is not authenticated");
    }


    private boolean isJobRunning(String jobName) {
        // Implement logic to check if the job is currently running
        // This could involve querying the JobRepository or using flags
        return false; // Placeholder
    }
    public void launchJob(String token) {
        // JWT에서 이메일을 추출
        String userEmail = jwtTokenProvider.getUserEmailFromJWT(token);

        // 이메일을 기반으로 사용자 ID 조회
        Long userId = userService.findUserIdByEmail(userEmail);

        // 현재 시간을 기반으로 한 타임스탬프를 JobParameters에 추가
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("token", token)
                .addLong("userId", userId)
                .addLong("timestamp", Instant.now().toEpochMilli()) // 고유성 추가
                .toJobParameters();

        // JobParameters 로그 추가
        System.out.println("Launching job with parameters: " + jobParameters.getParameters());

        try {
            jobLauncher.run(firstBatch.firstJob(), jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}