package com.example.vitalic_back.Service;

import com.example.vitalic_back.DTO.SignUpDto;
import com.example.vitalic_back.jwt.JwtTokenProvider;
import com.example.vitalic_back.repository.UserRepository;
import com.example.vitalic_back.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public Long SignUp(SignUpDto requestDto) throws Exception {
        if(userRepository.findByUserEmail(requestDto.getUserEmail()).isPresent()){
            throw new Exception("이미 존재하는 이메일입니다");
        }

        requestDto.validate(); // 비밀번호 검증

        User user = userRepository.save(requestDto.toEntity());
        user.encodePassword(passwordEncoder);

        user.addUserAuthority();
        return user.getUserNo();
    }

    @Override
    public String SignIn(Map<String, String> users){
        User user = userRepository.findByUserEmail(users.get("userEmail")).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 email 입니다"));

        String password = users.get("userPw");
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().name());

        return jwtTokenProvider.createToken(user.getUsername(), roles);
    }
}
