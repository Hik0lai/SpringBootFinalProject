package com.beehivemonitor.controller;

import com.beehivemonitor.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:5173")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWeather() {
        try {
            Map<String, Object> weatherData = weatherService.getWeatherData();
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            System.err.println("Error in WeatherController: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentWeather() {
        try {
            return ResponseEntity.ok(weatherService.getCurrentWeather());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getForecast() {
        try {
            return ResponseEntity.ok(weatherService.getForecast());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

