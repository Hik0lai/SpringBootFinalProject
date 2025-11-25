package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.HiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hives")
@CrossOrigin(origins = "http://localhost:5173")
public class HiveController {

    @Autowired
    private HiveService hiveService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }

    @GetMapping
    public ResponseEntity<List<Hive>> getAllHives(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(hiveService.getAllHivesByUser(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hive> getHiveById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(hiveService.getHiveById(id, email));
    }

    @PostMapping
    public ResponseEntity<Hive> createHive(@Valid @RequestBody Hive hive, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(hiveService.createHive(hive, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hive> updateHive(@PathVariable Long id, @Valid @RequestBody Hive hive, 
                                          @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(hiveService.updateHive(id, hive, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHive(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        hiveService.deleteHive(id, email);
        return ResponseEntity.noContent().build();
    }
}

