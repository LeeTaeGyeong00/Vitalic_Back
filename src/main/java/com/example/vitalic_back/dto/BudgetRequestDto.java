package com.example.vitalic_back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetRequestDto {
    private Long monthlyBudget;
    private Long dailyBudget;
    private Long weeklyBudget;
}