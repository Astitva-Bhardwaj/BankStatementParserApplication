package com.bankstatement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordGenerationRequest {
    private String firstName;
    private String dateOfBirth;  // Format: YYYY-MM-DD
    private String accountType;  // savings, current, etc.
    private String additionalInfo;
}