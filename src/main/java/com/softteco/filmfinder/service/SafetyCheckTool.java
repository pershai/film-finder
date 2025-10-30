package com.softteco.filmfinder.service;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SafetyCheckTool {
    private static final Set<String> BANNED_TERMS = Set.of(
            "delete", "drop", "update", "remove", "alter",
            "truncate", "insert", "create", "modify", "exec"
    );

    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile("(?i)(?:'|\";?\\s*(?:--|#|/\\*|\\\\*/|;|%00|\\x1a|\\x1b|\\x0d|\\x0a|\\x0c|\\x0b|\\x09|\\x20)*\\b(?:select|insert|update|delete|drop|truncate|union|create|alter|grant|revoke|exec|execute|shutdown|--|#))");

    @Tool
//    @Agent(value = "Validate the query for safety", outputKey = "validatedQuery")
    public String validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new SecurityException("Query cannot be empty");
        }

        String lowerQuery = query.toLowerCase();

        for (String term : BANNED_TERMS) {
            if (lowerQuery.contains(term)) {
                throw new SecurityException("Query contains potentially harmful term: " + term);
            }
        }

        if (SQL_INJECTION_PATTERN.matcher(lowerQuery).find()) {
            throw new SecurityException("Query contains potential SQL injection attempt");
        }

        if (lowerQuery.contains("<script>") || lowerQuery.contains("</script>")) {
            throw new SecurityException("Query contains potential XSS attempt");
        }

        return "Query is safe to process: " + query;
    }
}