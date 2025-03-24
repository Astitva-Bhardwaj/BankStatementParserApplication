package com.bankstatement.service;

import com.bankstatement.dto.PasswordGenerationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Slf4j
public class PasswordService {

    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_-+=<>?";

    private final Random random = new Random();

    public String generatePassword(PasswordGenerationRequest request) {
        log.info("Generating password for user with account type: {}", request.getAccountType());

        // Extract initial for first name
        String firstNameInitial = request.getFirstName().substring(0, 1).toUpperCase();

        // Extract year and month from date of birth
        LocalDate dob = LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_DATE);
        String yearMonth = String.format("%d%02d", dob.getYear() % 100, dob.getMonthValue());

        // Generate account type specific segment
        String accountTypeCode = getAccountTypeCode(request.getAccountType());

        // Generate random segment for added security (4 characters)
        String randomSegment = generateRandomSegment(4);

        // Generate special character
        String specialChar = getRandomChar(SPECIAL_CHARS);

        // Combine all segments
        return firstNameInitial + yearMonth + accountTypeCode + randomSegment + specialChar;
    }

    private String getAccountTypeCode(String accountType) {
        if (accountType == null) {
            return "GN"; // General
        }

        switch (accountType.toLowerCase()) {
            case "savings":
                return "SV";
            case "current":
                return "CR";
            case "salary":
                return "SL";
            case "fixed deposit":
            case "fd":
                return "FD";
            default:
                return accountType.substring(0, Math.min(2, accountType.length())).toUpperCase();
        }
    }

    private String generateRandomSegment(int length) {
        StringBuilder sb = new StringBuilder(length);

        // Ensure at least one uppercase letter
        sb.append(getRandomChar(UPPERCASE_CHARS));

        // Ensure at least one digit
        sb.append(getRandomChar(DIGITS));

        // Fill the rest with a mix of characters
        String allChars = LOWERCASE_CHARS + UPPERCASE_CHARS + DIGITS;
        for (int i = sb.length(); i < length; i++) {
            sb.append(getRandomChar(allChars));
        }

        // Shuffle the characters
        char[] chars = sb.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    private String getRandomChar(String chars) {
        return String.valueOf(chars.charAt(random.nextInt(chars.length())));
    }
}