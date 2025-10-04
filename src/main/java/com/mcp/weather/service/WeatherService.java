
package com.mcp.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public Map<String, Object> getWeather(String city) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Geocode city to get latitude and longitude
            String geocodeUrl = String.format("https://geocode.maps.co/search?q=%s", city);
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object>[] geocodeResponse = restTemplate.getForObject(geocodeUrl, Map[].class);
            if (geocodeResponse == null || geocodeResponse.length == 0) {
                result.put("error", "City not found.");
                return result;
            }
            double latitude = (double) geocodeResponse[0].get("lat");
            double longitude = (double) geocodeResponse[0].get("lon");

            // Fetch weather data using latitude and longitude
            String weatherUrl = String.format("%s?latitude=%f&longitude=%f&current_weather=true", API_URL, latitude, longitude);
            Map<String, Object> weatherResponse = restTemplate.getForObject(weatherUrl, Map.class);
            if (weatherResponse == null) {
                result.put("error", "Failed to retrieve weather data.");
                return result;
            }

            Map<String, Object> currentWeather = (Map<String, Object>) weatherResponse.get("current_weather");
            if (currentWeather == null) {
                result.put("error", "Current weather data not available.");
                return result;
            }

            result.put("city", city);
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
