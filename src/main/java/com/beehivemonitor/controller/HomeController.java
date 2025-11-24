package com.beehivemonitor.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@RestController
public class HomeController {

    @GetMapping(value = {"/", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> index() {
        try {
            Resource resource = new ClassPathResource("static/index.html");
            InputStream inputStream = resource.getInputStream();
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
            String html = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            return ResponseEntity.ok(html);
        } catch (IOException e) {
            return ResponseEntity.ok("<html><body><h1>Welcome to Beehive Monitor API</h1><p>Error loading page: " + e.getMessage() + "</p></body></html>");
        }
    }
}

