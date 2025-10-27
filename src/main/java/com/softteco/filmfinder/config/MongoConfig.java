package com.softteco.filmfinder.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Value("${spring.data.mongodb.host:localhost}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port:27017}")
    private int mongoPort;
    
    @Value("${spring.data.mongodb.username:}")
    private String username;
    
    @Value("${spring.data.mongodb.password:}")
    private String password;
    
    @Value("${spring.data.mongodb.authentication-database:admin}")
    private String authDb;

    @Bean
    MongoClient mongoClient() {
        String connectionString;
        if (!username.isEmpty() && !password.isEmpty()) {
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s&retryWrites=true&w=majority",
                username, password, mongoHost, mongoPort, authDb, authDb);
        } else {
            connectionString = String.format("mongodb://%s:%d", mongoHost, mongoPort);
        }
        return MongoClients.create(connectionString);
    }
}
