package com.softteco.filmfinder.state;

import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class GetSourcesNode implements NodeAction<MovieSearchState> {
    private final WatchmodeSearchSourcesTool sourcesTool;

    public GetSourcesNode(WatchmodeSearchSourcesTool sourcesTool) {
        this.sourcesTool = sourcesTool;
    }

    @Override
    public Map<String, Object> apply(MovieSearchState state) {
        try {
            var movieId = state.selectedMovie().get("id").toString();
            var sources = sourcesTool.getSources(movieId, "GB"); // region!
            return Map.of(
                    MovieSearchState.SOURCES_KEY, sources,
                    MovieSearchState.STEP_COUNT_KEY, state.stepCount() + 1
            );
        } catch (Exception e) {
            return Map.of(
                    MovieSearchState.ERROR_KEY, "Failed to get sources: " + e.getMessage()
            );
        }
    }
}