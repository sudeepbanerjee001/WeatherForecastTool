package com.mcp.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    private static final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String GEOCODE_API_URL = "https://geocoding-api.open-meteo.com/v1/search";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getWeather(String city) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Geocode city using Open-Meteo geocoding API
            String geocodeUrl = String.format("%s?name=%s&count=1&language=en&format=json", GEOCODE_API_URL, city);
            Map<String, Object> geocodeResponse = restTemplate.getForObject(geocodeUrl, Map.class);

            if (geocodeResponse == null || !geocodeResponse.containsKey("results")) {
                result.put("error", "City not found.");
                return result;
            }

            var results = (java.util.List<Map<String, Object>>) geocodeResponse.get("results");
            if (results.isEmpty()) {
                result.put("error", "City not found.");
                return result;
            }

            Map<String, Object> topResult = results.get(0);
            double latitude = ((Number) topResult.get("latitude")).doubleValue();
            double longitude = ((Number) topResult.get("longitude")).doubleValue();
            String cityName = topResult.get("name") + ", " + topResult.get("country");

            // 2. Fetch weather data using latitude and longitude
            String weatherUrl = String.format(
                    "%s?latitude=%f&longitude=%f&current_weather=true",
                    WEATHER_API_URL, latitude, longitude
            );
            Map<String, Object> weatherResponse = restTemplate.getForObject(weatherUrl, Map.class);

            if (weatherResponse == null || !weatherResponse.containsKey("current_weather")) {
                result.put("error", "Weather data not available.");
                return result;
            }

            Map<String, Object> currentWeather = (Map<String, Object>) weatherResponse.get("current_weather");

            // 3. Populate result
            result.put("city", cityName);
            result.put("temperature", currentWeather.get("temperature"));
            result.put("windspeed", currentWeather.get("windspeed"));
            result.put("weathercode", currentWeather.get("weathercode"));
            result.put("source", "Open-Meteo");

        } catch (Exception e) {
            result.put("error", "Failed to get weather: " + e.getMessage());
        }

        return result;
    }
}
