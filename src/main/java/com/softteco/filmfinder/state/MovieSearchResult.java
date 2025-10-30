package com.softteco.filmfinder.state;

import java.util.List;
import java.util.Map;

public record MovieSearchResult(
        List<Map<String, Object>> searchResults,
        Map<String, Object> movieDetails,
        List<Map<String, Object>> sources
) {}