package com.bankstatement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementRequest {
    private MultipartFile pdfFile;
    private String pdfFilePath;
    private String pdfPassword;
}