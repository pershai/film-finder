package com.softteco.filmfinder.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Duration.ofSeconds;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.model.name}")
    private String modelName;
    @Value("${gemini.model.temperature}")
    private double temperature;
    @Value("${gemini.model.topP}")
    private double topP;
    @Value("${gemini.model.topK}")
    private int topK;
    @Value("${gemini.model.outputTokens}")
    private int outputTokens;
    @Value("${gemini.model.timeout}")
    private int timeout;
    @Value("${gemini.model.logRequestsAndResponses}")
    private boolean logRequestsAndResponses;

    @Bean
    public ChatModel chatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .topP(topP)
                .topK(topK)
                .maxOutputTokens(outputTokens)
                .timeout(ofSeconds(timeout))
                .logRequestsAndResponses(logRequestsAndResponses)
                .build();
    }


}