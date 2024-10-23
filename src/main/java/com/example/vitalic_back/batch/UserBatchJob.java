package com.example.vitalic_back.batch;

import com.example.vitalic_back.entity.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class UserBatchJob {

    private final RestTemplate restTemplate;

    public UserBatchJob(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void fetchUserData(String token) {
        String apiUrl = "http://127.0.0.1:8000/api/user/1"; // 예시 API URL

        // 헤더에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // API 요청
        ResponseEntity<User> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, User.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            User user = response.getBody();
            // 사용자 데이터 처리
            System.out.println("사용자 정보: " + user);
        } else {
            System.out.println("사용자 정보 조회 실패: " + response.getStatusCode());
        }
    }
}