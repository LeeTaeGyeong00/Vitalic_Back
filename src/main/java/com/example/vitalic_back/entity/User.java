package com.example.vitalic_back.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USER")
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userNo;

    @Column(name = "userEmail", nullable = false, length = 80)
    private String userEmail;

    @Column(name = "userPW", nullable = false, length = 200)
    private String userPw;

    @Column(name = "userName", nullable = false, length = 200)
    private String userName;

    @Column(name = "userPH", nullable = false, length = 15)
    private String userPH;

    @Builder.Default
    @Column(name = "regDate", nullable = false)
    private LocalDate regDate = LocalDate.now(); // 테스트 임시

    @Column(name = "modDate")
    private LocalDateTime modDate;

    @Column(name ="userBudgetMonth")
    private Long userBudgetMonth;

    @Column(name ="userBudgetDay")
    private Long userBudgetDay;

    @Column(name ="userBudgetWeek")
    private Long userBudgetWeek;

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist // 테스트 임시
    public void prePersist() {
        if (this.regDate == null) {
            this.regDate = LocalDate.now(); 
        }
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.userPw = passwordEncoder.encode(userPw);
    }

    @Override
    public String getPassword() {
        return this.userPw;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }


    public void addUserAuthority() {
        this.role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한 반환 (예: ROLE_USER)
        return Collections.singleton(() -> "ROLE_" + this.role.name());
    }
}
