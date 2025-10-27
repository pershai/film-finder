package com.softteco.filmfinder.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class Movie {
    @CsvBindByPosition(position = 0)
    private String posterLink;

    @CsvBindByPosition(position = 1)
    private String title;

    @CsvBindByPosition(position = 2)
    private String year;

    @CsvBindByPosition(position = 3)
    private String certificate;

    @CsvBindByPosition(position = 4)
    private String runtime;

    @CsvBindByPosition(position = 5)
    private String genre;

    @CsvBindByPosition(position = 6)
    private String imdbRating;

    @CsvBindByPosition(position = 7)
    private String overview;

    @CsvBindByPosition(position = 8)
    private String metaScore;

    @CsvBindByPosition(position = 9)
    private String director;

    @CsvBindByPosition(position = 10)
    private String star1;

    @CsvBindByPosition(position = 11)
    private String star2;

    @CsvBindByPosition(position = 12)
    private String star3;

    @CsvBindByPosition(position = 13)
    private String star4;

    @CsvBindByPosition(position = 14)
    private String numberOfVotes;

    @CsvBindByPosition(position = 15)
    private String gross;
}
