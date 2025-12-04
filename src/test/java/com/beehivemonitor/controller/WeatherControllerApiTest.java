package com.beehivemonitor.controller;

import com.beehivemonitor.security.CustomUserDetailsService;
import com.beehivemonitor.security.JwtAuthenticationFilter;
import com.beehivemonitor.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for WeatherController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = WeatherController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class WeatherControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Map<String, Object> mockCurrentWeather;
    private Map<String, Object> mockForecast;
    private Map<String, Object> mockWeatherData;

    @BeforeEach
    void setUp() {
        // Mock current weather data
        mockCurrentWeather = new HashMap<>();
        mockCurrentWeather.put("city", "Stara Zagora");
        mockCurrentWeather.put("country", "BG");
        mockCurrentWeather.put("temperature", 25.5);
        mockCurrentWeather.put("feelsLike", 26.0);
        mockCurrentWeather.put("humidity", 65);
        mockCurrentWeather.put("pressure", 1013);
        mockCurrentWeather.put("description", "clear sky");
        mockCurrentWeather.put("icon", "01d");
        mockCurrentWeather.put("windSpeed", 3.5);

        // Mock forecast data
        mockForecast = new HashMap<>();
        mockForecast.put("city", "Stara Zagora");
        mockForecast.put("country", "BG");
        mockForecast.put("forecast", new java.util.ArrayList<>());

        // Mock combined weather data
        mockWeatherData = new HashMap<>();
        mockWeatherData.put("current", mockCurrentWeather);
        mockWeatherData.put("forecast", mockForecast);
    }

    @Test
    void testGetWeather_Success() throws Exception {
        // Arrange
        when(weatherService.getWeatherData()).thenReturn(mockWeatherData);

        // Act & Assert
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current").exists())
                .andExpect(jsonPath("$.forecast").exists())
                .andExpect(jsonPath("$.current.city").value("Stara Zagora"))
                .andExpect(jsonPath("$.current.temperature").value(25.5));
    }

    @Test
    void testGetCurrentWeather_Success() throws Exception {
        // Arrange
        when(weatherService.getCurrentWeather()).thenReturn(mockCurrentWeather);

        // Act & Assert
        mockMvc.perform(get("/api/weather/current"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("Stara Zagora"))
                .andExpect(jsonPath("$.temperature").value(25.5))
                .andExpect(jsonPath("$.humidity").value(65))
                .andExpect(jsonPath("$.description").value("clear sky"));
    }

    @Test
    void testGetForecast_Success() throws Exception {
        // Arrange
        when(weatherService.getForecast()).thenReturn(mockForecast);

        // Act & Assert
        mockMvc.perform(get("/api/weather/forecast"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("Stara Zagora"))
                .andExpect(jsonPath("$.forecast").exists());
    }

    @Test
    void testGetWeather_ServiceException_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(weatherService.getWeatherData())
                .thenThrow(new RuntimeException("Failed to fetch weather data"));

        // Act & Assert
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetCurrentWeather_ServiceException_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(weatherService.getCurrentWeather())
                .thenThrow(new RuntimeException("Failed to fetch current weather"));

        // Act & Assert
        mockMvc.perform(get("/api/weather/current"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetForecast_ServiceException_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(weatherService.getForecast())
                .thenThrow(new RuntimeException("Failed to fetch forecast"));

        // Act & Assert
        mockMvc.perform(get("/api/weather/forecast"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetWeather_EmptyData_Success() throws Exception {
        // Arrange
        Map<String, Object> emptyData = new HashMap<>();
        when(weatherService.getWeatherData()).thenReturn(emptyData);

        // Act & Assert
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

