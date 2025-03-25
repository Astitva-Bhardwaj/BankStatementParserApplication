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

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmService {

    private final OpenAiService openAiService;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.timeout}")
    private int timeout;

    @Value("${prompt.template.path}")
    private String promptTemplatePath;

    public Map<String, Object> extractBankStatementInfo(String pdfText) throws IOException {
        log.error("OpenAI Configuration Details:");
        log.error("Model: {}", model);
        log.error("API Key Present: {}", apiKey != null && !apiKey.isEmpty());
        log.error("Timeout: {} seconds", timeout);

        try {
            String promptTemplate = loadPromptTemplate();
            String prompt = String.format(promptTemplate, pdfText);

            List<ChatMessage> messages = List.of(
                    new ChatMessage("system", "You are a financial document parser specialized in extracting structured information from bank statements."),
                    new ChatMessage("user", prompt)
            );

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(0.1)
                    .build();

            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            return objectMapper.readValue(response, Map.class);

        } catch (Exception e) {
            log.error("Detailed OpenAI API Error:", e);
            throw new IOException("Failed to process bank statement with OpenAI: " + e.getMessage(), e);
        }
    }

    private String loadPromptTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + promptTemplatePath);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}