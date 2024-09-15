package com.example.vitalic_back.Service;

import com.example.vitalic_back.entity.User;
import com.example.vitalic_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 이메일을 찾지 못했습니다 " + username));
        return new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getPassword(), user.getAuthorities());
    }

}
