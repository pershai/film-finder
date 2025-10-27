package com.softteco.filmfinder.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Duration.ofSeconds;

@Configuration
public class EmbeddingConfig {

    @Value("${hugging_face.api.key}")
    private String HF_API_KEY;
    @Value("${hugging_face.model.name}")
    private String model;

    @Bean
    public EmbeddingModel embeddingModel() {
        return HuggingFaceEmbeddingModel.builder()
                .accessToken(HF_API_KEY)
                .modelId(model)
                .waitForModel(true)
                .timeout(ofSeconds(60))
                .build();
    }
}

