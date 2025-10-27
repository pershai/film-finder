package com.softteco.filmfinder.service;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;

public interface MovieSupervisor {
    @Agent
    String invoke(@V("request") String request);
}