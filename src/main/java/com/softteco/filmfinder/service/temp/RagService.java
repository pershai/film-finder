package com.softteco.filmfinder.service.temp;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final EmbeddingModel embeddingModel;
    private final MongoDbEmbeddingStore embeddingStore;

    @Value("${app.embedding.maxResults:3}")
    private int maxResults;

    @Value("${app.embedding.minScore:3}")
    private double minScore;

    public List<String> findRelevantDocuments(String query) {
        EmbeddingSearchRequest request = getEmbeddingSearchRequest(query);

        EmbeddingSearchResult<TextSegment> searched = embeddingStore.search(request);
        List<EmbeddingMatch<TextSegment>> matches = searched.matches();
        return matches.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.toList());
    }

    public List<String> findRelevantFilms(String query) {
        log.info("Searching for movies with query: {}", query);

        EmbeddingSearchRequest request = getEmbeddingSearchRequest(query);

        EmbeddingSearchResult<TextSegment> searched = embeddingStore.search(request);
        List<EmbeddingMatch<TextSegment>> matches = searched.matches();
        // Process and format the results
        List<String> results = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> match : matches) {
            TextSegment segment = match.embedded();
            double score = match.score();

            // Get metadata from the text segment
            String title = segment.metadata().getString("title");
            String year = segment.metadata().getString("year");
            String genre = segment.metadata().getString("genre");
            String director = segment.metadata().getString("director");
            String rating = segment.metadata().getString("imdbRating");

            // Format the result
            String result = String.format(
                    "Title: %s (%s) | Genre: %s | Director: %s | Rating: %s | Match: %.2f%%\n" +
                    "Overview: %s\n",
                    title, year, genre, director, rating, score * 100,
                    segment.text()
            );

            results.add(result);

            log.debug("Found match: {} (score: {})", title, score);
        }

        if (results.isEmpty()) {
            results.add("No relevant movies found for your query: " + query);
        }

        log.info("Found {} relevant movies for query: {}", results.size(), query);
        return results;
    }

    private EmbeddingSearchRequest getEmbeddingSearchRequest(String query) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        return EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }
}
