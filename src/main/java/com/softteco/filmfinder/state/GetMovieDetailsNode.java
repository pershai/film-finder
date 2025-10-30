package com.softteco.filmfinder.state;

import com.softteco.filmfinder.service.WatchmodeSearchTool;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class GetMovieDetailsNode implements NodeAction<MovieSearchState> {
    private final WatchmodeSearchTool watchmodeTool;

    public GetMovieDetailsNode(WatchmodeSearchTool watchmodeTool) {
        this.watchmodeTool = watchmodeTool;
    }

    @Override
    public Map<String, Object> apply(MovieSearchState state) {
        try {
            var selectedMovie = state.selectedMovie();
            // Use the selectedMovie map directly to get the id
            var movieId = selectedMovie.get("id");
            if (movieId == null) {
                throw new IllegalStateException("Selected movie is missing 'id' field");
            }
            var details = watchmodeTool.searchByTitle(movieId.toString());
            return Map.of(
                    MovieSearchState.MOVIE_DETAILS_KEY, details,
                    MovieSearchState.STEP_COUNT_KEY, state.stepCount() + 1
            );
        } catch (Exception e) {
            return Map.of(
                    MovieSearchState.ERROR_KEY, "Failed to get movie details: " + e.getMessage()
            );
        }
    }
}