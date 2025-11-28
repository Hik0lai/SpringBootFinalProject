package com.beehivemonitor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String CITY = "Stara Zagora";
    private static final String COUNTRY_CODE = "BG";

    public Map<String, Object> getCurrentWeather() {
        try {
            // URL encode the city name to handle spaces
            String encodedCity = URLEncoder.encode(CITY, StandardCharsets.UTF_8);
            String url = String.format("%s/weather?q=%s,%s&appid=%s&units=metric&lang=en",
                    BASE_URL, encodedCity, COUNTRY_CODE, apiKey);
            
            System.out.println("Fetching weather from: " + url.replace(apiKey, "API_KEY_HIDDEN"));
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from OpenWeatherMap API");
            }
            
            JsonNode json = objectMapper.readTree(response);
            
            // Check for API errors in response
            if (json.has("cod") && json.get("cod").asInt() != 200) {
                String message = json.has("message") ? json.get("message").asText() : "Unknown error";
                throw new RuntimeException("OpenWeatherMap API error: " + message);
            }

            Map<String, Object> currentWeather = new HashMap<>();
            currentWeather.put("city", json.get("name").asText());
            currentWeather.put("country", json.get("sys").get("country").asText());
            currentWeather.put("temperature", json.get("main").get("temp").asDouble());
            currentWeather.put("feelsLike", json.get("main").get("feels_like").asDouble());
            currentWeather.put("humidity", json.get("main").get("humidity").asInt());
            currentWeather.put("pressure", json.get("main").get("pressure").asInt());
            currentWeather.put("description", json.get("weather").get(0).get("description").asText());
            currentWeather.put("icon", json.get("weather").get(0).get("icon").asText());
            currentWeather.put("windSpeed", json.get("wind").get("speed").asDouble());
            currentWeather.put("windDirection", json.get("wind").has("deg") ? json.get("wind").get("deg").asInt() : null);
            currentWeather.put("cloudiness", json.get("clouds").get("all").asInt());
            currentWeather.put("visibility", json.has("visibility") ? json.get("visibility").asInt() / 1000.0 : null);
            currentWeather.put("sunrise", json.get("sys").get("sunrise").asLong());
            currentWeather.put("sunset", json.get("sys").get("sunset").asLong());

            return currentWeather;
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error fetching current weather: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Invalid API key. Please check your OpenWeatherMap API key.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("City not found. Please check the city name.");
            }
            throw new RuntimeException("Failed to fetch current weather: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("RestClient error: " + e.getMessage());
            throw new RuntimeException("Network error connecting to OpenWeatherMap: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error fetching current weather: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch current weather: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getForecast() {
        try {
            // URL encode the city name to handle spaces
            String encodedCity = URLEncoder.encode(CITY, StandardCharsets.UTF_8);
            String url = String.format("%s/forecast?q=%s,%s&appid=%s&units=metric&lang=en",
                    BASE_URL, encodedCity, COUNTRY_CODE, apiKey);
            
            System.out.println("Fetching forecast from: " + url.replace(apiKey, "API_KEY_HIDDEN"));
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from OpenWeatherMap API");
            }
            
            JsonNode json = objectMapper.readTree(response);
            
            // Check for API errors in response
            if (json.has("cod") && json.get("cod").asInt() != 200) {
                String message = json.has("message") ? json.get("message").asText() : "Unknown error";
                throw new RuntimeException("OpenWeatherMap API error: " + message);
            }

            Map<String, Object> forecastData = new HashMap<>();
            forecastData.put("city", json.get("city").get("name").asText());
            forecastData.put("country", json.get("city").get("country").asText());

            List<Map<String, Object>> forecastList = new ArrayList<>();
            JsonNode list = json.get("list");

            for (JsonNode item : list) {
                Map<String, Object> forecastItem = new HashMap<>();
                forecastItem.put("dateTime", item.get("dt").asLong());
                forecastItem.put("temperature", item.get("main").get("temp").asDouble());
                forecastItem.put("feelsLike", item.get("main").get("feels_like").asDouble());
                forecastItem.put("humidity", item.get("main").get("humidity").asInt());
                forecastItem.put("pressure", item.get("main").get("pressure").asInt());
                forecastItem.put("description", item.get("weather").get(0).get("description").asText());
                forecastItem.put("icon", item.get("weather").get(0).get("icon").asText());
                forecastItem.put("windSpeed", item.get("wind").get("speed").asDouble());
                forecastItem.put("windDirection", item.get("wind").has("deg") ? item.get("wind").get("deg").asInt() : null);
                forecastItem.put("cloudiness", item.get("clouds").get("all").asInt());
                forecastItem.put("precipitation", item.has("rain") ? item.get("rain").has("3h") ? item.get("rain").get("3h").asDouble() : 0 : 0);

                forecastList.add(forecastItem);
            }

            forecastData.put("forecast", forecastList);
            return forecastData;
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error fetching forecast: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Invalid API key. Please check your OpenWeatherMap API key.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("City not found. Please check the city name.");
            }
            throw new RuntimeException("Failed to fetch forecast: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("RestClient error: " + e.getMessage());
            throw new RuntimeException("Network error connecting to OpenWeatherMap: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error fetching forecast: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch forecast: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getWeatherData() {
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("current", getCurrentWeather());
        weatherData.put("forecast", getForecast());
        return weatherData;
    }
}

