package com.softteco.filmfinder.controller;

import com.softteco.filmfinder.service.LangGraphMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies/graph")
@RequiredArgsConstructor
@Slf4j
public class MovieGraphController {

    private final LangGraphMovieService langGraphMovieService;

    @GetMapping("/find")
    public ResponseEntity<String> find(@RequestParam String query) {
//        try {
//            String result = langGraphMovieService.searchWithGraph(query);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("Graph search error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Graph search failed: " + e.getMessage());
//        }
        return ResponseEntity.ok("");
    }
}


