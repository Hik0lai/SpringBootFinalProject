package com.beehivemonitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> index() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Beehive Monitor API");
        response.put("status", "running");
        response.put("frontend", "Please use the React frontend at http://localhost:5173");
        return ResponseEntity.ok(response);
    }
}

