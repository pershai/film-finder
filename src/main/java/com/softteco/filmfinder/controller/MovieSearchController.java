package com.softteco.filmfinder.controller;

import com.softteco.filmfinder.state.MovieSearchResult;
import com.softteco.filmfinder.state.MovieSearchStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies/graph")
public class MovieSearchController {

    private final MovieSearchStateService movieSearchService;

    public MovieSearchController(MovieSearchStateService movieSearchService) {
        this.movieSearchService = movieSearchService;
    }

    @GetMapping("/state")
    public ResponseEntity<MovieSearchResult> searchMovies(@RequestParam String query) {
        try {
            MovieSearchResult result = movieSearchService.searchMovies(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
