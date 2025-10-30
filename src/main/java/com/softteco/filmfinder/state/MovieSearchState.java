package com.softteco.filmfinder.state;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MovieSearchState extends AgentState {
    public static final String QUERY_KEY = "query";
    public static final String SEARCH_RESULTS_KEY = "search_results";
    public static final String SELECTED_MOVIE_KEY = "selected_movie";
    public static final String MOVIE_DETAILS_KEY = "movie_details";
    public static final String SOURCES_KEY = "sources";
    public static final String STEP_COUNT_KEY = "step_count";
    public static final String ERROR_KEY = "error";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            QUERY_KEY, Channels.base((Supplier<String>) () -> ""),
            SEARCH_RESULTS_KEY, Channels.base((Supplier<List<Map<String, Object>>>) ArrayList::new),
            SELECTED_MOVIE_KEY, Channels.base((Supplier<Map<String, Object>>) Map::of),
            MOVIE_DETAILS_KEY, Channels.base((Supplier<Map<String, Object>>) Map::of),
            SOURCES_KEY, Channels.base((Supplier<List<Map<String, Object>>>) ArrayList::new),
            STEP_COUNT_KEY, Channels.base((Supplier<Integer>) () -> 0),
            ERROR_KEY, Channels.base((Supplier<String>) () -> "")
    );

    public MovieSearchState(Map<String, Object> initData) {
        super(initData);
    }

    // Getters for state values
    public String query() {
        return this.<String>value(QUERY_KEY).orElse("");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchResults() {
        return this.<List<Map<String, Object>>>value(SEARCH_RESULTS_KEY).orElse(List.of());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> selectedMovie() {
        return this.<Map<String, Object>>value(SELECTED_MOVIE_KEY).orElse(Map.of());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> movieDetails() {
        return this.<Map<String, Object>>value(MOVIE_DETAILS_KEY).orElse(Map.of());
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> sources() {
        return this.<List<Map<String, Object>>>value(SOURCES_KEY).orElse(List.of());
    }

    public int stepCount() {
        return this.<Integer>value(STEP_COUNT_KEY).orElse(0);
    }

    public String error() {
        return this.<String>value(ERROR_KEY).orElse("");
    }
}