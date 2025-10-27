package com.softteco.filmfinder.config;

import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import com.softteco.filmfinder.service.WatchmodeSearchTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MovieAgentConfig {

    @Value("${watchmode.api.key}")
    private String watchmodeApiKey;

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    public WatchmodeSearchTool watchmodeSearchTool(WebClient webClient) {
        return new WatchmodeSearchTool(webClient, watchmodeApiKey);
    }

    @Bean
    public WatchmodeSearchSourcesTool watchmodeSearchSourcesTool(WebClient webClient) {
        return new WatchmodeSearchSourcesTool(webClient, watchmodeApiKey);
    }
}