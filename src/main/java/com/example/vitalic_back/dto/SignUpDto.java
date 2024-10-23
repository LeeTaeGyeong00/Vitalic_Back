package com.example.vitalic_back.dto;

import com.example.vitalic_back.entity.Role;
import com.example.vitalic_back.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    @NotBlank(message = "아이디 입력")
    private String userEmail;

    @NotBlank(message = "비밀번호 입력")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String userPw;

    @NotBlank(message = "이름 입력")
    private String userName;

    @NotBlank(message = "전화번호 입력")
    private String userPH;

    private String checkedPassword;

    private Role role;

    public void validate() {
        if (!userPw.equals(checkedPassword)) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }

    public User toEntity(){
        return User.builder()
                .userEmail(userEmail)
                .userName(userName)
                .userPw(userPw)
                .userPH(userPH)
                .role(role != null ? role : Role.USER)
                .build();
    }

}
