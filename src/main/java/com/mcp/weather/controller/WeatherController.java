
package com.mcp.weather.controller;

import com.mcp.weather.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/invoke")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping
    public Map<String, Object> handleInvocation(@RequestBody Map<String, Object> payload) {
        String action = (String) payload.getOrDefault("action", "");
        if ("getWeather".equalsIgnoreCase(action)) {
            String city = (String) payload.getOrDefault("city", "Unknown");
            return weatherService.getWeather(city);
        }
        return Map.of("error", "Unknown action: " + action);
    }
}
