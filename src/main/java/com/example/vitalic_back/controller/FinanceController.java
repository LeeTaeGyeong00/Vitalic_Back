package com.example.vitalic_back.controller;

import com.example.vitalic_back.entity.EnterPassbook;
import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.repository.EnterPassbookRepository;
import com.example.vitalic_back.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
public class FinanceController {

    @Autowired
    private EnterPassbookRepository enterPassbookRepository;
    private final JobLauncher jobLauncher;
    private final UserService userService;
    private final JobRegistry jobRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    public FinanceController(JobLauncher jobLauncher, UserService userService, JobRegistry jobRegistry, JwtTokenProvider jwtTokenProvider) {
        this.jobLauncher = jobLauncher;
        this.userService = userService;
        this.jobRegistry = jobRegistry;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/first")
    public String firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
        return "ok";
    }
    @PostMapping("/insert")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한이 있어야 접근 가능
    public ResponseEntity<EnterPassbook> createEnterPassbook(@RequestBody EnterPassbook enterPassbook) {
        // JSON 데이터를 EnterPassbook 엔티티로 매핑하여 저장
        EnterPassbook savedEntry = enterPassbookRepository.save(enterPassbook);
        return new ResponseEntity<>(savedEntry, HttpStatus.CREATED);
    }

    @PostMapping("/compare-budget")
    public ResponseEntity<String> compareBudget(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getUserPk(token);
        Long userId = userService.findUserIdByEmail(email);

        userService.compareUserBudgetWithDjango(userId);

        return ResponseEntity.ok("Budget comparison complete");
    }
}