package com.bankstatement.service;

import com.bankstatement.dto.BankStatementRequest;
import com.bankstatement.dto.BankStatementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class BankStatementService {

    private final PdfService pdfService;
    private final LlmService llmService;

    public BankStatementResponse parseBankStatement(BankStatementRequest request) {
        try {
            // Extract text from PDF
            String pdfText;
            if (request.getPdfFile() != null) {
                pdfText = pdfService.extractTextFromPdf(request.getPdfFile(), request.getPdfPassword());
            } else if (request.getPdfFilePath() != null) {
                pdfText = pdfService.extractTextFromPdf(request.getPdfFilePath(), request.getPdfPassword());
            } else {
                throw new IllegalArgumentException("Either PDF file or file path must be provided");
            }

            // Use LLM to extract structured information from the PDF text
            Map<String, Object> extractedInfo = llmService.extractBankStatementInfo(pdfText);

            // Map extracted information to response DTO
            return mapToResponse(extractedInfo);

        } catch (IOException e) {
            log.error("Failed to process bank statement", e);
            throw new RuntimeException("Failed to process bank statement: " + e.getMessage(), e);
        }
    }

    private BankStatementResponse mapToResponse(Map<String, Object> extractedInfo) {
        return BankStatementResponse.builder()
                .name(getString(extractedInfo, "name"))
                .email(getString(extractedInfo, "email"))
                .openingBalance(getDouble(extractedInfo, "openingBalance"))
                .closingBalance(getDouble(extractedInfo, "closingBalance"))
                .accountNumber(getString(extractedInfo, "accountNumber"))
                .statementPeriod(getString(extractedInfo, "statementPeriod"))
                .currency(getString(extractedInfo, "currency"))
                .build();
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            try {
                return Double.parseDouble(value.toString().replaceAll("[^\\d.]", ""));
            } catch (NumberFormatException e) {
                log.warn("Could not parse value for key {} as double: {}", key, value);
                return null;
            }
        }
    }
}