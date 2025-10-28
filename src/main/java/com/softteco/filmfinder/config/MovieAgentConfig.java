package com.softteco.filmfinder.config;

import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import com.softteco.filmfinder.service.WatchmodeSearchTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MovieAgentConfig {

    @Value("${watchmode.api.key}")
    private String watchmodeApiKey;

    @Bean
    public WatchmodeSearchTool watchmodeSearchTool(RestTemplate webClient) {
        return new WatchmodeSearchTool(webClient, watchmodeApiKey);
    }

    @Bean
    public WatchmodeSearchSourcesTool watchmodeSearchSourcesTool(RestTemplate webClient) {
        return new WatchmodeSearchSourcesTool(webClient, watchmodeApiKey);
    }
}