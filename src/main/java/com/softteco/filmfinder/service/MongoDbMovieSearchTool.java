package com.softteco.filmfinder.service;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.V;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MongoDbMovieSearchTool {
    private final EmbeddingModel embeddingModel;
    private final MongoDbEmbeddingStore embeddingStore;

    @Value("${app.embedding.maxResults:5}")
    private int maxResults;

    @Value("${app.embedding.minScore:0.7}")
    private double minScore;

    @Agent(value = "Search for movies by description or plot", name = "search", outputKey = "movieTitle")
    public Mono<String> search(@V("query") String query) {
        return Mono.fromCallable(() -> embeddingModel.embed(query).content())
                .flatMap(queryEmbedding -> {
                    EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                            .queryEmbedding(queryEmbedding)
                            .maxResults(maxResults)
                            .minScore(minScore)
                            .build();
                    
                    return Mono.fromCallable(() -> embeddingStore.search(request))
                            .map(result -> formatSearchResults(result.matches()));
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("Error searching for movies: " + e.getMessage(), e)));
    }

    private String formatSearchResults(List<EmbeddingMatch<TextSegment>> matches) {
        if (matches == null || matches.isEmpty()) {
            return "No matching movies found.";
        }
        
        return matches.stream()
                .map(match -> String.format("- %s (Score: %.2f)", 
                        match.embedded().text(), 
                        match.score()))
                .collect(Collectors.joining("\n"));
    }
}
