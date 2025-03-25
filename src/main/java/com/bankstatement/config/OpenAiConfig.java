package com.bankstatement.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.timeout:30}")
    private int timeout;

    @Bean
    public OpenAiService openAiService() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API Key is not configured. Please set OPENAI_API_KEY environment variable.");
        }
        return new OpenAiService(apiKey, Duration.ofSeconds(timeout));
    }
}