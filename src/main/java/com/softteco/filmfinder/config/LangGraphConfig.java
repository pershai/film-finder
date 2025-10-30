package com.softteco.filmfinder.config;

import com.softteco.filmfinder.service.MongoDbMovieSearchTool;
import com.softteco.filmfinder.service.SafetyCheckTool;
import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import com.softteco.filmfinder.service.WatchmodeSearchTool;
import dev.langchain4j.model.chat.ChatModel;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.agentexecutor.AgentExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangGraphConfig {

    @Bean
    public CompiledGraph<AgentExecutor.State> movieAgentExecutor(
            ChatModel chatModel,
            MongoDbMovieSearchTool mongoDbMovieSearchTool,
            WatchmodeSearchTool watchmodeSearchTool,
            WatchmodeSearchSourcesTool watchmodeSearchSourcesTool) {

        try {
            return AgentExecutor.builder()
                    .chatModel(chatModel)
                    .toolsFromObject(mongoDbMovieSearchTool)
                    .toolsFromObject(watchmodeSearchTool)
                    .toolsFromObject(watchmodeSearchSourcesTool)
                    .build()
                    .compile();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compile LangGraph AgentExecutor", e);
        }
    }
}

