package com.softteco.filmfinder.controller;

import com.softteco.filmfinder.service.temp.RagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class SearchController {

    private final RagService ragService;

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam String query) {
        List<String> results = ragService.findRelevantDocuments(query);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/find")
    public ResponseEntity<List<String>> find(@RequestParam String query) {
        List<String> results = ragService.findRelevantFilms(query);
        return ResponseEntity.ok(results);
    }


}
