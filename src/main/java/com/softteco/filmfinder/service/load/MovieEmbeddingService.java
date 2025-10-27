package com.softteco.filmfinder.service.load;

import com.opencsv.bean.CsvToBeanBuilder;
import com.softteco.filmfinder.model.Movie;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public record MovieEmbeddingService(
        EmbeddingStore<TextSegment> embeddingStore,
        EmbeddingModel embeddingModel) {

    public List<Movie> ingestMoviesFromCsv() {
        List<Movie> movies = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("imdb_top_1000.csv")) {
            if (inputStream == null) throw new RuntimeException("imdb_top_1000.csv not found");

            movies = new CsvToBeanBuilder<Movie>(new InputStreamReader(inputStream))
                    .withType(Movie.class)
                    .build()
                    .parse();

            log.info("Processing {} movies...", movies.size());

            for (Movie movie : movies) {
                if (movie.getTitle() == null || movie.getOverview() == null) continue;

                Metadata metadata = getMetadata(movie);
                TextSegment segment = TextSegment.from(movie.getOverview(), metadata);
                Embedding embedding = embeddingModel.embed(segment).content();

                embeddingStore.add(embedding, segment);
                log.info("Stored: {}", movie.getTitle());
            }
        } catch (Exception e) {
            log.error("Error processing CSV: {}", e.getMessage());
        }
        return movies;
    }

    private static Metadata getMetadata(Movie movie) {
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("title", movie.getTitle());
        metadataMap.put("year", movie.getYear());
        metadataMap.put("genre", movie.getGenre());
        metadataMap.put("director", movie.getDirector());
        metadataMap.put("imdbRating", movie.getImdbRating());
        metadataMap.put("star1", movie.getStar1());
        metadataMap.put("star2", movie.getStar2());

        return new Metadata(metadataMap);
    }
}
