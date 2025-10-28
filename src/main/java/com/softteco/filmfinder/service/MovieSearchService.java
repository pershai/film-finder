package com.softteco.filmfinder.service;

import com.softteco.filmfinder.util.RetryUtils;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieSearchService {
    private final MongoDbMovieSearchTool movieSearchTool;
    private final WatchmodeSearchTool watchmodeSearchTool;
    private final WatchmodeSearchSourcesTool watchmodeSearchSourcesTool;
    private final ChatModel chatModel;

    @Value("${app.retry.maxAttempts:3}")
    private int maxRetryAttempts;

    @Value("${app.retry.initialDelay:1000}")
    private long initialRetryDelayMs;

    @Value("${app.retry.multiplier:2.0}")
    private double retryMultiplier;

    public String search(String request) {
        try {
            return RetryUtils.withRetry(
                createSearchOperation(request),
                maxRetryAttempts,
                Duration.ofMillis(initialRetryDelayMs),
                retryMultiplier,
                this::shouldRetry
            );
        } catch (Exception e) {
            log.error("Failed to process search request after {} attempts: {}", maxRetryAttempts, e.getMessage(), e);
            return "Sorry, we encountered an error while processing your request. Please try again later.";
        }
    }

    private Callable<String> createSearchOperation(String request) {
        return () -> {
            log.info("Processing search request: {}", request);

            // Create the supervisor with all the tools
            MovieSupervisor supervisor = AgenticServices
                    .supervisorBuilder(MovieSupervisor.class)
                    .subAgents(
                            movieSearchTool,
                            watchmodeSearchTool,
                            watchmodeSearchSourcesTool
                    )
                    .supervisorContext("""
                            You are a movie assistant that helps users find movies and where to watch them.
                            
                            Follow these steps:
                            1. If the user gives a plot/description, call MongoDbMovieSearchTool to find the movie.
                            2. Use WatchmodeSearchTool with the title to get a Watchmode ID.
                            3. Use WatchmodeSearchSourcesTool with that ID and the user's region to get streaming info.
                            4. Return a clean human-readable response.
                            
                            Always return a clean, user-friendly response with the results.
                            """)
                    .chatModel(chatModel)
                    .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                    .build();

            String result = supervisor.invoke(request);
            log.info("Search completed successfully");
            return result;
        };
    }

    private boolean shouldRetry(Exception e) {
        // Retry on network issues or other retryable exceptions
        return e instanceof ResourceAccessException || 
               (e instanceof RuntimeException && !(e instanceof IllegalArgumentException));
    }
}
