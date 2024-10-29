package com.example.vitalic_back.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private Long userId;

    public CustomUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}