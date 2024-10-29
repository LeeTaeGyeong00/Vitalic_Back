package com.example.vitalic_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;  // 사용자 고유 번호

    @Column(nullable = false)
    private String token;  // JWT 토큰

    // 기본 생성자와 getter, setter
    public UserToken() {}
    public UserToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
