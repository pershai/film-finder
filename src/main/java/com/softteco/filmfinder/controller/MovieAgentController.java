package com.softteco.filmfinder.controller;

import com.softteco.filmfinder.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieAgentController {
    private final MovieSearchService movieSearchService;

    @GetMapping("/find")
    public ResponseEntity<String> findAndStreamMovie(@RequestParam String query) {

        try {
            String result = movieSearchService.search(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error finding movie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Sorry, I encountered an error while processing your request.");
        }
    }
}