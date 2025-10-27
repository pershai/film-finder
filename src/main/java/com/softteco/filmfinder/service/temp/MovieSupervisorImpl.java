package com.softteco.filmfinder.service.temp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softteco.filmfinder.service.MongoDbMovieSearchTool;
import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import com.softteco.filmfinder.service.WatchmodeSearchTool;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieSupervisorImpl {

    @Value("${watchmode.region}")
    private String DEFAULT_REGION;
    private static final String MOVIE_NOT_FOUND_MSG = "I couldn't find any movies matching your description.";
    private static final String NO_STREAMING_INFO_MSG = "I found '%s' but couldn't find streaming information.";
    private static final String ERROR_MSG = "Sorry, I encountered an error while processing your request.";

    private final MongoDbMovieSearchTool movieSearchTool;
    private final WatchmodeSearchTool watchmodeSearchTool;
    private final WatchmodeSearchSourcesTool watchmodeSearchSourcesTool;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public String findAndStreamMovie(String query, String region) {
        if (!StringUtils.hasText(query)) {
            log.warn("Empty query provided");
            return MOVIE_NOT_FOUND_MSG;
        }

        try {
            String safeRegion = StringUtils.hasText(region) ? region : DEFAULT_REGION;
            log.debug("Searching for movie with query: '{}' in region: {}", query, safeRegion);

            return findMovieAndStreamingInfo(query, safeRegion);
        } catch (Exception e) {
            log.error("Error finding movie for query: {}", query, e);
            return ERROR_MSG;
        }
    }

    private String findMovieAndStreamingInfo(String query, String region) {
        // Step 1: Find movies matching the description
        String searchResults = movieSearchTool.search(query);
        log.debug("Search results: {}", searchResults);

        // Step 2: Get the first movie's title
        String firstMovieTitle = extractFirstMovieTitle(searchResults);
        if (firstMovieTitle == null) {
            log.info("No movies found for query: {}", query);
            return MOVIE_NOT_FOUND_MSG;
        }

        // Step 3: Search Watchmode for the movie and process reactively
        return watchmodeSearchTool.searchByTitle(firstMovieTitle)
                .onErrorResume(e -> {
                    log.error("Error searching Watchmode for movie: {}", firstMovieTitle, e);
                    return Mono.just("");
                })
                .flatMap(watchmodeResults -> {
                    log.debug("Watchmode search results: {}", watchmodeResults);
                    return Mono.justOrEmpty(findAndFormatStreamingInfo(firstMovieTitle, watchmodeResults, region));
                })
                .defaultIfEmpty(String.format(NO_STREAMING_INFO_MSG, firstMovieTitle))
                .block();
    }

    private String findAndFormatStreamingInfo(String movieTitle, String watchmodeResults, String region) {
        return extractWatchmodeId(watchmodeResults)
                .map(watchmodeId -> {
                    log.debug("Found Watchmode ID: {} for movie: {}", watchmodeId, movieTitle);
                    return watchmodeSearchSourcesTool.getSources(watchmodeId, region)
                            .map(sources -> {
                                log.debug("Streaming sources: {}", sources);
                                return formatFinalResponse(movieTitle, sources);
                            })
                            .onErrorResume(e -> {
                                log.error("Error getting streaming sources for movie: {}", movieTitle, e);
                                return Mono.just(String.format("Error getting streaming information for '%s'", movieTitle));
                            })
                            .block();
                })
                .orElseGet(() -> {
                    log.info("No Watchmode ID found for movie: {}", movieTitle);
                    return String.format(NO_STREAMING_INFO_MSG, movieTitle);
                });
    }

    private String extractFirstMovieTitle(String searchResults) {
        if (!StringUtils.hasText(searchResults)) {
            return null;
        }

        try {
            String firstLine = searchResults.split("\n")[0];
            if (firstLine.startsWith("Title: ")) {
                return firstLine
                        .replace("Title: ", "")
                        .split(" \\(")[0]
                        .trim();
            }
        } catch (Exception e) {
            log.error("Error extracting movie title from: " + searchResults, e);
        }
        return null;
    }

    private Optional<String> extractWatchmodeId(String watchmodeResults) {
        if (!StringUtils.hasText(watchmodeResults)) {
            return Optional.empty();
        }

        try {
            log.debug("Raw watchmode response: {}", watchmodeResults);

            JsonNode root = objectMapper.readTree(watchmodeResults);
            if (root.isObject()) {
                for (String arrayField : Arrays.asList("results", "title_results", "titles", "items")) {
                    if (root.has(arrayField) && root.get(arrayField).isArray() && !root.get(arrayField).isEmpty()) {
                        JsonNode firstResult = root.get(arrayField).get(0);
                        JsonNode idNode = firstResult.path("id");
                        if (!idNode.isMissingNode()) {
                            return Optional.ofNullable(idNode.asText());
                        }
                    }
                }
                if (root.has("id")) {
                    return Optional.ofNullable(root.get("id").asText());
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing Watchmode results", e);
        }
        return Optional.empty();
    }

    private String formatFinalResponse(String title, String sources) {
        if (!StringUtils.hasText(sources)) {
            return String.format("I found '%s' but no streaming information is available.", title);
        }

        String prompt = String.format("""
            I found the movie '%s'. Here's the streaming information:
            %s
            
            Please format this into a nice, human-readable response.
            If the sources are in JSON format, extract and format them by service.
            Include any relevant details like price, quality, and direct links if available.
            """, title, sources);

        try {
            return chatModel.chat(prompt);
        } catch (Exception e) {
            log.error("Error generating response with Gemini", e);
            return String.format("I found '%s' with the following streaming options: %s",
                    title, sources);
        }
    }
}