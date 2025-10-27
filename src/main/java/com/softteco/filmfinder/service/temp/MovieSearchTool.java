package com.softteco.filmfinder.service.temp;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;

public interface MovieSearchTool {
    @Agent(value = "Search for movies by description or plot", outputKey = "movieResults")
    String search(@V("query") String query);
}
