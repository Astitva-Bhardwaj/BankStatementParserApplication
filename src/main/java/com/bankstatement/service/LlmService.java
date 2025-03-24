package com.bankstatement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with LLM APIs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

    private final OpenAiService openAiService;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.timeout}")
    private int timeout;

    @Value("${prompt.template.path}")
    private String promptTemplatePath;

    /**
     * Extract structured information from bank statement text using LLM
     *
     * @param pdfText Extracted text from bank statement PDF
     * @return Map containing structured data extracted by the LLM
     * @throws IOException If there's an error processing the request
     */
    public Map<String, Object> extractBankStatementInfo(String pdfText) throws IOException {
        log.info("Extracting structured information from bank statement using LLM");

        String promptTemplate = loadPromptTemplate();
        String prompt = String.format(promptTemplate, pdfText);

        List<ChatMessage> messages = List.of(
                new ChatMessage("system", "You are a financial document parser specialized in extracting structured information from bank statements."),
                new ChatMessage("user", prompt)
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(0.1) // Low temperature for more deterministic output
                .build();

        String response = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();

        try {
            // Parse the JSON response into a Map
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse LLM response as JSON", e);
            throw new IOException("Failed to parse structured data from LLM response", e);
        }
    }

    /**
     * Load the prompt template from resources
     *
     * @return Prompt template string
     * @throws IOException If there's an error loading the template
     */
    private String loadPromptTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + promptTemplatePath);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}