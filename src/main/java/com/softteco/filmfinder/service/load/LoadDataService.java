package com.softteco.filmfinder.service.load;

import com.softteco.filmfinder.model.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoadDataService {
    private final MongoTemplate mongoTemplate;
    private final MovieEmbeddingService movieEmbeddingService;
    @Value("${vector.store.collection:embeddings}")
    private String collectionName;

    public List<Movie> loadDataFromCSV() {
        if (isDataLoaded()) {
            log.info("Data already exists in the database. Skipping data load.");
            return List.of();
        }

        log.info("Loading data...");
        List<Movie> movies = movieEmbeddingService.ingestMoviesFromCsv();
        log.info("Successfully loaded {} movies", movies.size());
        return movies;
    }

    private boolean isDataLoaded() {
        try {
            return mongoTemplate.collectionExists(collectionName) &&
                   mongoTemplate.getCollection(collectionName).countDocuments() > 0;
        } catch (Exception e) {
            log.error("Error checking if data is loaded", e);
            return false;
        }
    }
}
