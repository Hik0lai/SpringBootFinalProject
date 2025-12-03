package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.HiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hives")
@CrossOrigin(origins = "http://localhost:5173")
public class HiveController {

    @Autowired
    private HiveService hiveService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }
    
    private User.Role getUserRole(String email) {
        return userRepository.findByEmail(email)
            .map(User::getRole)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<Hive>> getAllHives(@RequestHeader("Authorization") String token) {
        getEmailFromToken(token); // Validate token
        return ResponseEntity.ok(hiveService.getAllHives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hive> getHiveById(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        getEmailFromToken(token); // Validate token
        return ResponseEntity.ok(hiveService.getHiveById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hive> createHive(@Valid @RequestBody Hive hive, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        User.Role role = getUserRole(email);
        return ResponseEntity.ok(hiveService.createHive(hive, email, role));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hive> updateHive(@PathVariable UUID id, @Valid @RequestBody Hive hive, 
                                          @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        User.Role role = getUserRole(email);
        return ResponseEntity.ok(hiveService.updateHive(id, hive, role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHive(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        User.Role role = getUserRole(email);
        hiveService.deleteHive(id, role);
        return ResponseEntity.noContent().build();
    }
}

