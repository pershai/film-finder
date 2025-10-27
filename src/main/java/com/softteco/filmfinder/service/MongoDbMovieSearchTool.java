package com.softteco.filmfinder.service;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.V;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public String search(@V("query") String query) {
        try {
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(maxResults)
                    .minScore(minScore)
                    .build();

            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
            return formatSearchResults(result.matches());
//            return extractTitle(result.matches());
        } catch (Exception e) {
            throw new RuntimeException("Error searching for movies: " + e.getMessage(), e);
        }
    }

    private String formatSearchResults(List<EmbeddingMatch<TextSegment>> matches) {
        if (matches == null || matches.isEmpty()) {
            return "No movies found matching your query.";
        }

        return matches.stream()
                .map(match -> String.format(
                        "Title: %s (%s) | Score: %.2f",
                        match.embedded().metadata().getString("title"),
                        match.embedded().metadata().getString("year"),
                        match.score()
                ))
                .collect(Collectors.joining("\n"));
    }

    private String extractTitle(List<EmbeddingMatch<TextSegment>> matches) {
        if (matches == null || matches.isEmpty()) {
            return "No movies found matching your query.";
        }

        return matches.stream()
                .map(match -> match.embedded().metadata())
                .filter(metadata -> metadata != null && metadata.getString("title") != null)
                .map(metadata -> metadata.getString("title"))
                .findFirst()
                .orElse("No valid movie title found.");
    }
}
