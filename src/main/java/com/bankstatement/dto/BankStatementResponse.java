package com.bankstatement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementResponse {
    private String name;
    private String email;
    private Double openingBalance;
    private Double closingBalance;
    private String accountNumber;
    private String statementPeriod;
    private String currency;
}