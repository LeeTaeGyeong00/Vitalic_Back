package com.example.vitalic_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "FinanceAnalysisThisMonth")
public class FinanceAnalysisThisMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long thisMonthNo;
    //출금의 합
    @Column(name = "totalWithdrawal")
    private Long totalWithdrawal;
    //입금의 합
    @Column(name = "totalDeposit")
    private Long totalDeposit;
    // 고정 지출
    @Column(name = "fixedExpenditure")
    private Long fixedExpenditure;


}