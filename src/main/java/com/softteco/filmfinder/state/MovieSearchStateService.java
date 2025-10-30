package com.softteco.filmfinder.state;

import org.bsc.langgraph4j.CompiledGraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieSearchStateService {
    private final CompiledGraph<MovieSearchState> movieSearchGraph;

    public MovieSearchStateService(CompiledGraph<MovieSearchState> movieSearchGraph) {
        this.movieSearchGraph = movieSearchGraph;
    }

    public MovieSearchResult searchMovies(String query) {
        var initialState = Map.of(
                MovieSearchState.QUERY_KEY, query,
                MovieSearchState.SEARCH_RESULTS_KEY, new ArrayList<Map<String, Object>>(),
                MovieSearchState.SELECTED_MOVIE_KEY, Map.<String, Object>of(),
                MovieSearchState.MOVIE_DETAILS_KEY, Map.<String, Object>of(),
                MovieSearchState.SOURCES_KEY, new ArrayList<Map<String, Object>>(),
                MovieSearchState.STEP_COUNT_KEY, 0,
                MovieSearchState.ERROR_KEY, ""
        );

        MovieSearchState result = movieSearchGraph.invoke(initialState)
                .orElseThrow(() -> new RuntimeException("Failed to execute movie search"));

        List<Map<String, Object>> searchResultsList;
        try {
            searchResultsList = result.searchResults() != null ?
                    result.searchResults() : new ArrayList<>();
        } catch (Exception e) {
            searchResultsList = null;
        }
        String searchResults;
        try {
            searchResults = result.searchResults() != null ?
                    result.searchResults().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")) :
                    "";
        } catch (Exception e) {
            searchResults = "";
        }


        Map<String, Object> movieDetails = result.movieDetails() != null ?
                result.movieDetails() : Map.of();
        List<Map<String, Object>> sources = result.sources() != null ?
                result.sources() : new ArrayList<>();

        return new MovieSearchResult(searchResultsList != null ? searchResultsList :
                List.of(Map.of(MovieSearchState.SEARCH_RESULTS_KEY, searchResults)),
                movieDetails, sources);
    }
}
