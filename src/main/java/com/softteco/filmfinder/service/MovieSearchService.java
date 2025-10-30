package com.softteco.filmfinder.service;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieSearchService {
    private final MongoDbMovieSearchTool movieSearchTool;
    private final WatchmodeSearchTool watchmodeSearchTool;
    private final WatchmodeSearchSourcesTool watchmodeSearchSourcesTool;
    private final ChatModel chatModel;

    public record MovieSearchResult(
            List<Map<String, Object>> searchResults,
            Map<String, Object> movieDetails,
            List<Map<String, Object>> sources
    ) {}

    public String search(String request) {
        try {
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

        } catch (Exception e) {
            log.error("Error in MovieSearchService: {}", e.getMessage(), e);
            return "Sorry, I encountered an error while processing your request: " + e.getMessage();
        }
    }
}
