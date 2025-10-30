package com.softteco.filmfinder.state;

import com.softteco.filmfinder.service.MongoDbMovieSearchTool;
import com.softteco.filmfinder.state.MovieSearchState;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class SearchMoviesNode implements NodeAction<MovieSearchState> {
    private final MongoDbMovieSearchTool searchTool;

    public SearchMoviesNode(MongoDbMovieSearchTool searchTool) {
        this.searchTool = searchTool;
    }

    @Override
    public Map<String, Object> apply(MovieSearchState state) {
        try {
            String query = state.query();
            var results = searchTool.search(query);
            return Map.of(
                    MovieSearchState.SEARCH_RESULTS_KEY, results,
                    MovieSearchState.STEP_COUNT_KEY, state.stepCount() + 1
            );
        } catch (Exception e) {
            return Map.of(
                    MovieSearchState.ERROR_KEY, "Search failed: " + e.getMessage()
            );
        }
    }
}




