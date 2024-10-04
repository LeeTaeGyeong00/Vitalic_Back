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

//-- 월급 입금
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 4586400, 0, '내 신한은행 110490816690', '대림대학교', 1664659200000, 0, 2000000, 6586400);
//
//        -- Netflix 출금
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6586400, 1, '넷플릭스', '내 신한은행 110490816690', 1664659200000, 0, 13500, 6572900);
//
//        -- YouTube Premium 출금
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6572900, 1, '유튜브 프리미엄', '내 신한은행 110490816690', 1664659200000, 0, 20400, 6552500);
//
//        -- SKT 통신 출금
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6552500, 1, 'SKT 통신', '내 신한은행 110490816690', 1664659200000, 0, 32000, 6520500);
//
//        -- KB손해보험 출금
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6520500, 1, 'KB손해보험', '내 신한은행 110490816690', 1664659200000, 0, 50000, 6470500);
//
//        -- 신한 적금 출금
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6470500, 1, '신한 적금', '내 신한은행 110490816690', 1664659200000, 0, 300000, 6170500);
//
//        -- 편의점 소비 (GS25)
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6170500, 1, 'GS25', '내 신한은행 110490816690', 1664659200000, 1, 7000, 6163500);
//
//        -- 카페 소비 (스타벅스)
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6163500, 1, '스타벅스', '내 신한은행 110490816690', 1664659200000, 1, 12000, 6151500);
//
//        -- 식당 소비 (부대찌개집)
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6151500, 1, '부대찌개집', '내 신한은행 110490816690', 1664659200000, 1, 18000, 6133500);
//
//        -- 영화관 소비 (CGV)
//        INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)
//        VALUES ('신한은행', '110490816690', 6133500, 1, 'CGV', '내 신한은행 110490816690', 1664659200000, 1, 15000, 6118500);
