package com.example.vitalic_back.service;

import com.example.vitalic_back.entity.User;
import com.example.vitalic_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Service("userDetailService")
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUserEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자 이메일을 찾지 못했습니다 " + username));
//        return new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getPassword(), user.getAuthorities());
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 이메일을 찾지 못했습니다 " + username));

        // CustomUserDetails를 반환
        return new CustomUserDetails(user.getUserNo(), user.getUserEmail(), user.getPassword(), user.getAuthorities());
    }
}
