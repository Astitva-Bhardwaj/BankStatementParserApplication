package com.bankstatement.controller;

import com.bankstatement.dto.BankStatementRequest;
import com.bankstatement.dto.BankStatementResponse;
import com.bankstatement.dto.PasswordGenerationRequest;
import com.bankstatement.service.BankStatementService;
import com.bankstatement.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/bank-statements")
@RequiredArgsConstructor
@Slf4j
public class BankStatementController {

    private final BankStatementService bankStatementService;
    private final PasswordService passwordService;

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BankStatementResponse> parseBankStatementFromUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "password", required = false) String password) {

        log.info("Received request to parse bank statement PDF from upload");

        BankStatementRequest request = BankStatementRequest.builder()
                .pdfFile(file)
                .pdfPassword(password)
                .build();

        BankStatementResponse response = bankStatementService.parseBankStatement(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/parse-from-path")
    public ResponseEntity<BankStatementResponse> parseBankStatementFromPath(
            @RequestParam("filePath") String filePath,
            @RequestParam(value = "password", required = false) String password) {

        log.info("Received request to parse bank statement PDF from path: {}", filePath);

        BankStatementRequest request = BankStatementRequest.builder()
                .pdfFilePath(filePath)
                .pdfPassword(password)
                .build();

        BankStatementResponse response = bankStatementService.parseBankStatement(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/generate-password")
    public ResponseEntity<String> generatePassword(@RequestBody PasswordGenerationRequest request) {
        log.info("Received request to generate password for user: {}", request.getFirstName());

        String password = passwordService.generatePassword(request);
        return ResponseEntity.ok(password);
    }
}