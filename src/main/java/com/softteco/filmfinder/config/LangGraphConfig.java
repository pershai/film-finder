package com.softteco.filmfinder.config;

import com.softteco.filmfinder.service.MongoDbMovieSearchTool;
import com.softteco.filmfinder.service.WatchmodeSearchSourcesTool;
import com.softteco.filmfinder.service.WatchmodeSearchTool;
import com.softteco.filmfinder.state.GetMovieDetailsNode;
import com.softteco.filmfinder.state.GetSourcesNode;
import com.softteco.filmfinder.state.MovieSearchState;
import com.softteco.filmfinder.state.SearchMoviesNode;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Predicate;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
@Configuration
public class LangGraphConfig {

//    @Bean
//    public CompiledGraph<AgentExecutor.State> movieAgentExecutor(
//            ChatModel chatModel,
//            MongoDbMovieSearchTool mongoDbMovieSearchTool,
//            WatchmodeSearchTool watchmodeSearchTool,
//            WatchmodeSearchSourcesTool watchmodeSearchSourcesTool) {
//
//        try {
//            return AgentExecutor.builder()
//                    .chatModel(chatModel)
//                    .toolsFromObject(mongoDbMovieSearchTool)
//                    .toolsFromObject(watchmodeSearchTool)
//                    .toolsFromObject(watchmodeSearchSourcesTool)
//                    .build()
//                    .compile();
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to compile LangGraph AgentExecutor", e);
//        }
//    }

    @Bean
    public CompiledGraph<MovieSearchState> movieSearchGraph(
            MongoDbMovieSearchTool mongoDbMovieSearchTool,
            WatchmodeSearchTool watchmodeSearchTool,
            WatchmodeSearchSourcesTool watchmodeSearchSourcesTool) {

        // Create node actions
        var searchNode = new SearchMoviesNode(mongoDbMovieSearchTool);
        var detailsNode = new GetMovieDetailsNode(watchmodeSearchTool);
        var sourcesNode = new GetSourcesNode(watchmodeSearchSourcesTool);

        // Build the graph
        try {
            return new StateGraph<>(MovieSearchState.SCHEMA, MovieSearchState::new)
                    // Add nodes
                    .addNode("search", node_async(searchNode))
                    .addNode("get_details", node_async(detailsNode))
                    .addNode("get_sources", node_async(sourcesNode))
                    .addNode("error", node_async(state -> {
                        log.error("Error in workflow: {}", state.error());
                        return Map.of();
                    }))

                    // Define edges - fixed to use proper conditional edges
                    .addEdge(START, "search")
                    .addEdge("search", "get_details")
                    .addEdge("get_details", "get_sources")
                    .addEdge("get_sources", END)
                    .addEdge("error", END)

                    // Compile the graph
                    .compile();

        } catch (Exception e) {
            log.error("Failed to compile movie search graph", e);
            throw new IllegalStateException("Failed to compile movie search graph", e);
        }
    }
}

