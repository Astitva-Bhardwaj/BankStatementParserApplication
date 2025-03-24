package com.bankstatement.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class PdfService {

    public String extractTextFromPdf(MultipartFile file, String password) throws IOException {
        log.info("Extracting text from uploaded PDF file");

        try (PDDocument document = loadPdfDocument(file, password)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public String extractTextFromPdf(String filePath, String password) throws IOException {
        log.info("Extracting text from PDF file at path: {}", filePath);

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        try (PDDocument document = loadPdfDocument(path.toFile(), password)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private PDDocument loadPdfDocument(MultipartFile file, String password) throws IOException {
        try {
            // Create a temporary file from the uploaded file
            Path tempFile = Files.createTempFile("temp-", ".pdf");
            file.transferTo(tempFile.toFile());

            return loadPdfDocument(tempFile.toFile(), password);
        } catch (InvalidPasswordException e) {
            log.error("Invalid PDF password provided", e);
            throw new IOException("Invalid PDF password", e);
        }
    }

    private PDDocument loadPdfDocument(File file, String password) throws IOException {
        try {
            if (password != null && !password.isEmpty()) {
                return PDDocument.load(file, password);
            } else {
                return PDDocument.load(file);
            }
        } catch (InvalidPasswordException e) {
            log.error("PDF is encrypted and requires a password", e);
            throw new IOException("PDF is encrypted and requires a password", e);
        }
    }
}