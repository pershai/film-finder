package com.softteco.filmfinder.config;

import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import com.softteco.filmfinder.service.WatchmodeSearchTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableReactiveMongoAuditing
public class MovieAgentConfig {

    @Value("${watchmode.api.key}")
    private String watchmodeApiKey;

    @Bean
    public WatchmodeSearchTool watchmodeSearchTool(WebClient webClient) {
        return new WatchmodeSearchTool(webClient, watchmodeApiKey);
    }

    @Bean
    public WatchmodeSearchSourcesTool watchmodeSearchSourcesTool(WebClient webClient) {
        return new WatchmodeSearchSourcesTool(webClient, watchmodeApiKey);
    }
}