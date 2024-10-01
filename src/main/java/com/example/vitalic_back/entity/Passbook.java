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
    @Column
    private Long tran_id;
    //은행 기관 명
    @Column(name = "bank_name")
    private String bank_name;
    //계좌 잔액
    @Column(name ="balance_amt")
    private Long balance_amt;
    //입출금 구분 0: 입금 1: 출금
    @Column(name = "inout_type")
    private Long inout_type;
    //거래 일자
    @Column(name = "tran_date_time")
    private Long tran_date_time;
    //거래 구분 0: 통장 1: 카드
    @Column(name = "tran_type")
    private Long tran_type;
    //통장인자내용 이거는 추후에 이야기 나눠봐야할듯
    @Column(name = "print_content")
    private String print_content;
    //거래 금액
    @Column(name ="tran_amt")
    private Long tran_amt;
    //거래 후 잔액
    @Column(name ="after_balance_amt")
    private Long after_balance_amt;
}
