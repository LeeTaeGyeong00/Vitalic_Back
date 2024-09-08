package com.example.vitalic_back.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userNo;

    @Column(name = "userEmail", nullable = false, length = 80)
    private Long userEmail;

    @Column(name = "userPW", nullable = false, length = 200)
    private Long userPw;

    @Column(name = "userName", nullable = false, length = 200)
    private Long userName;

    @Column(name = "userPH", nullable = false, length = 15)
    private Long userPH;

    @Column(name = "regDate", nullable = false)
    private LocalDate regDate;

    @Column(name = "modDate")
    private LocalDateTime modDate;



}
