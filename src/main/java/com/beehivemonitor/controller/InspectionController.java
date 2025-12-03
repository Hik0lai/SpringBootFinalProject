package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Inspection;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.InspectionService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "http://localhost:5173")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private HiveRepository hiveRepository;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }

    @GetMapping
    public ResponseEntity<List<InspectionResponse>> getAllInspections(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        List<Inspection> inspections = inspectionService.getAllInspectionsByUser(email);
        
        List<InspectionResponse> responses = inspections.stream()
            .map(insp -> new InspectionResponse(
                insp.getId(),
                insp.getHive().getId(),
                insp.getHive().getName(),
                insp.getInspector(),
                insp.getDate().toString(),
                insp.getNotes()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionResponse> getInspectionById(@PathVariable UUID id, 
                                                               @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        Inspection inspection = inspectionService.getInspectionById(id, email);
        return ResponseEntity.ok(new InspectionResponse(
            inspection.getId(),
            inspection.getHive().getId(),
            inspection.getHive().getName(),
            inspection.getInspector(),
            inspection.getDate().toString(),
            inspection.getNotes()
        ));
    }

    @PostMapping
    public ResponseEntity<InspectionResponse> createInspection(@Valid @RequestBody InspectionRequest request,
                                                              @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        
        Inspection inspection = new Inspection();
        Hive hive = hiveRepository.findById(request.getHiveId())
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        inspection.setHive(hive);
        inspection.setInspector(request.getInspector());
        inspection.setDate(java.time.LocalDate.parse(request.getDate()));
        inspection.setNotes(request.getNotes());
        
        inspection = inspectionService.createInspection(inspection, email);
        return ResponseEntity.ok(new InspectionResponse(
            inspection.getId(),
            inspection.getHive().getId(),
            inspection.getHive().getName(),
            inspection.getInspector(),
            inspection.getDate().toString(),
            inspection.getNotes()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectionResponse> updateInspection(@PathVariable UUID id,
                                                              @Valid @RequestBody InspectionRequest request,
                                                              @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        
        Inspection inspection = new Inspection();
        inspection.setInspector(request.getInspector());
        inspection.setDate(java.time.LocalDate.parse(request.getDate()));
        inspection.setNotes(request.getNotes());
        
        inspection = inspectionService.updateInspection(id, inspection, email);
        return ResponseEntity.ok(new InspectionResponse(
            inspection.getId(),
            inspection.getHive().getId(),
            inspection.getHive().getName(),
            inspection.getInspector(),
            inspection.getDate().toString(),
            inspection.getNotes()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInspection(@PathVariable UUID id,
                                                  @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        inspectionService.deleteInspection(id, email);
        return ResponseEntity.noContent().build();
    }

    // DTOs for request/response
    @Data
    public static class InspectionRequest {
        private UUID hiveId;
        private String inspector;
        private String date;
        private String notes;
    }

    public static class InspectionResponse {
        public UUID id;
        public UUID hiveId;
        public String hiveName;
        public String inspector;
        public String date;
        public String notes;

        public InspectionResponse(UUID id, UUID hiveId, String hiveName, String inspector, String date, String notes) {
            this.id = id;
            this.hiveId = hiveId;
            this.hiveName = hiveName;
            this.inspector = inspector;
            this.date = date;
            this.notes = notes;
        }
    }
}

