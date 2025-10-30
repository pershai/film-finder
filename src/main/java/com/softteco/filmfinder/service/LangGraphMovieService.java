package com.softteco.filmfinder.service;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.agentexecutor.AgentExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LangGraphMovieService {

    private final CompiledGraph<AgentExecutor.State> movieAgentExecutor;

    public String searchWithGraph(String query) {
        var input = Map.<String, Object>of("messages", UserMessage.from(query));
        Object result = movieAgentExecutor.invoke(input);
        return result != null ? result.toString() : "";
    }
}


