package com.softteco.filmfinder.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.mongodb.IndexMapping;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

@Configuration
public class VectorStoreConfig {


    @Value("${spring.data.mongodb.database:film_finder}")
    private String databaseName;

    @Value("${vector.store.collection:embeddings}")
    private String collectionName;

    public static final String indexName = "vector_index";
    @Autowired
    EmbeddingModel embeddingModel;

    @Autowired
    MongoClient mongoClient;


    @Bean
    public MongoDbEmbeddingStore mongoDbEmbeddingStore() {
        boolean shouldCreateIndex = checkIndexExists(mongoClient);

        IndexMapping indexMapping = IndexMapping.builder()
                .dimension(embeddingModel.dimension())
                .metadataFieldNames(new HashSet<>())
                .build();

        return MongoDbEmbeddingStore.builder()
                .fromClient(mongoClient)
                .databaseName(databaseName)
                .collectionName(collectionName)
                .createIndex(shouldCreateIndex)
                .indexName(indexName)
                .indexMapping(indexMapping)
                .build();
    }

    public boolean checkIndexExists(MongoClient mongoClient) {
        try {
            // Get the list of indexes for the collection
            for (Document index : mongoClient.getDatabase(databaseName)
                    .getCollection(collectionName)
                    .listIndexes()) {
                // Check if any index has the same name as our indexName
                if (indexName.equals(index.getString("name"))) {
                    return false; // Index exists, no need to create
                }
            }
        } catch (Exception e) {
            // If there's an error (like collection doesn't exist), we should create the index
            return true;
        }
        return true; // No matching index found, should create
    }

}
