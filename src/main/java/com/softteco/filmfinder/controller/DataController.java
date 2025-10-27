package com.softteco.filmfinder.controller;

import com.softteco.filmfinder.model.Movie;
import com.softteco.filmfinder.service.load.LoadDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
@AllArgsConstructor
public class DataController {

    private final LoadDataService loadDataService;

    @GetMapping("/load")
    public ResponseEntity<Integer> load() {
        List<Movie> results = loadDataService.loadDataFromCSV();
        return ResponseEntity.ok(results.size());
    }


}
