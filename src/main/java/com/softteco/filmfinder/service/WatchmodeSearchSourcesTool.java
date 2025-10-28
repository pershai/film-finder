package com.softteco.filmfinder.service;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WatchmodeSearchSourcesTool {
    private final RestTemplate webClient;
    private final String apiKey;

//    @Agent(value = "Get streaming availability for a Watchmode ID", outputKey = "streamingSources")
//    public Mono<String> getSources(@V("watchmodeId") String watchmodeId, @V("region") String region) {
//        String url = String.format("/title/%s/sources/?apiKey=%s&regions=%s",
//                watchmodeId, apiKey, region);
//
//        return webClient.get()
//                .uri(url)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @Agent(value = "Get streaming availability for a Watchmode ID", outputKey = "streamingSources")
    public String getSources(@V("watchmodeId") String watchmodeId, @V("region") String region) {
        String url = String.format("https://api.watchmode.com/v1/title/%s/sources/?apiKey=%s&regions=%s",
                watchmodeId, apiKey, region);

        return webClient.getForObject(url, String.class);
    }
}