package com.softteco.filmfinder.service;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class WatchmodeSearchTool {
    private final RestTemplate webClient;
    private final String apiKey;

//    @Agent(value = "Search for a movie on Watchmode by title", outputKey = "watchmodeResults")
//    public Mono<String> searchByTitle(@V("title") String title) {
//        try {
//            String url = String.format("/search/?apiKey=%s&search_field=name&search_value=%s",
//                    apiKey, URLEncoder.encode(title, StandardCharsets.UTF_8));
//
//            return webClient.get()
//                    .uri(url)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .retrieve()
//                    .bodyToMono(String.class);
//        } catch (Exception e) {
//            return Mono.error(new RuntimeException("Error searching for movie: " + e.getMessage(), e));
//        }
//    }

    @Agent(value = "Search for a movie on Watchmode by title", outputKey = "watchmodeResults")
    public String searchByTitle(@V("title") String title) {
        try {
            String url = String.format("https://api.watchmode.com/v1/search/?apiKey=%s&search_field=name&search_value=%s",
                    apiKey, URLEncoder.encode(title, StandardCharsets.UTF_8));
            return webClient.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error searching for movie: " + e.getMessage(), e);
        }
    }
}
