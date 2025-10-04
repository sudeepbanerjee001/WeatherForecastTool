package com.mcp.weather.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ToolRegistrar {

    // MCP Server registration endpoint
    private static final String MCP_URL = "http://localhost:8080/mcp/registerTool";

    @PostConstruct
    public void registerTool() {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> registration = new HashMap<>();
        registration.put("toolName", "Weather Forecast Tool");
        registration.put("endpoint", "http://localhost:8082/invoke");
        registration.put("description", "Provides weather forecast information for any city");
        registration.put("intents", List.of("getWeather"));  // <-- Add supported intents here

        try {
            String response = restTemplate.postForObject(MCP_URL, registration, String.class);
            System.out.println("✅ Successfully registered Weather Forecast Tool with MCP Server: " + response);
        } catch (Exception e) {
            System.err.println("❌ Failed to register tool with MCP Server: " + e.getMessage());
        }
    }
}
