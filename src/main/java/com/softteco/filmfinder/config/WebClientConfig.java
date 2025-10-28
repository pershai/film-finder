package com.softteco.filmfinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public RestTemplate webClient() {
        return new RestTemplate();
//        return WebClient.builder()
//                .baseUrl("https://api.watchmode.com/v1")
//                .build();
    }
}
