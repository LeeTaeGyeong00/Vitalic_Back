package com.example.vitalic_back.jwt;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenStorage {

    private final ConcurrentHashMap<Long, String> tokenStore = new ConcurrentHashMap<>();

    // JWT 저장
    public void storeToken(Long userId, String token) {
        tokenStore.put(userId, token);
    }

    // JWT 조회
    public String getToken(Long userId) {
        return tokenStore.get(userId);
    }

    // JWT 제거
    public void removeToken(Long userId) {
        tokenStore.remove(userId);
    }    // 현재 활성화된 사용자 ID 가져오기 (가장 최근 로그인된 사용자 ID)
    public Long getActiveUserId() {
        return tokenStore.keys().nextElement(); // 또는 다른 방법으로 현재 사용자를 선택
    }

    // 만료된 JWT 삭제 (스케줄링, 예: 매일 1회 실행)
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void removeExpiredTokens() {
        tokenStore.clear(); // 만료된 토큰만 지우도록 구현 가능
    }
}