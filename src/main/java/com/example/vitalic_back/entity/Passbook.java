package com.example.vitalic_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Passbook")
public class Passbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //은행 기관 명
    @Column(name = "bank_name")
    private String bank_name;
    //계좌번호
    @Column(name ="account_number")
    private String account_number;
    //계좌 잔액
    @Column(name ="balance_amt")
    private Long balance_amt;
    //입출금 구분 0: 입금 1: 출금
    @Column(name = "inout_type")
    private Long inout_type;
    //입금처
    @Column(name = "in_des")
    private String in_des;
    //출금처
    @Column(name = "out_des")
    private String out_des;
    //출금처 카테고리 0:입금 1:이체, 2:편의점, 3:마트, 4:웹쇼핑,
    // 5:엔터테인먼트(영화,게임), 6: 카페, 7:패스트푸트, 8:식당, 9:기타
    //0 1 3 6 7 8 2 4 5
    @Column(name = "out_type")
    private int out_type;
    //거래 일자
    @Column(name = "tran_date_time")
    private LocalDateTime  tran_date_time;
    //거래 구분 0: 통장 1: 카드
    @Column(name = "tran_type")
    private Long tran_type;
    //거래 금액
    @Column(name ="tran_amt")
    private Long tran_amt;
    //거래 후 잔액
    @Column(name ="after_balance_amt")
    private Long after_balance_amt;
}