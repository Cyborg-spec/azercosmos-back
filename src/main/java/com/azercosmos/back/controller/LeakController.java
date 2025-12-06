package com.azercosmos.back.controller;

import com.azercosmos.back.dto.LeakDTO;
import com.azercosmos.back.enums.LeakStatus;
import com.azercosmos.back.scheduler.SatelliteScanScheduler;
import com.azercosmos.back.service.LeakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Methane Leaks", description = "API for managing methane leak detections")
public class LeakController {

    private final LeakService leakService;
    private final SatelliteScanScheduler satelliteScanScheduler;

    @Operation(summary = "Get all leaks", description = "Retrieves a list of all detected methane leaks")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all leaks")
    @GetMapping
    public ResponseEntity<List<LeakDTO>> getAllLeaks() {
        log.info("Fetching all leaks");
        List<LeakDTO> leaks = leakService.getAllLeaks();
        return ResponseEntity.ok(leaks);
    }

    @Operation(summary = "Get leak by ID", description = "Retrieves a single leak by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leak found"),
            @ApiResponse(responseCode = "404", description = "Leak not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeakDTO> getLeakById(
            @Parameter(description = "Leak ID (e.g., LK-2025-001)") @PathVariable String id) {
        log.info("Fetching leak: {}", id);
        return leakService.getLeakById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create leak manually", description = "Creates a new leak record (simulates AI detection)")
    @ApiResponse(responseCode = "201", description = "Leak created successfully")
    @PostMapping
    public ResponseEntity<LeakDTO> createLeak(
            @Parameter(description = "Leak data") @Valid @RequestBody LeakDTO leakDTO) {
        log.info("Creating new leak at: {}", leakDTO.getLocationName());
        LeakDTO created = leakService.createLeak(leakDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update leak status", description = "Updates the status of an existing leak (NEW, DISPATCHED, VERIFIED, FALSE_POSITIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Leak not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<LeakDTO> updateLeakStatus(
            @Parameter(description = "Leak ID") @PathVariable String id,
            @Parameter(description = "New status") @RequestParam LeakStatus status) {
        log.info("Updating leak {} status to {}", id, status);
        return leakService.updateLeakStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Run AI detection", description = "Triggers a mock satellite scan and AI analysis. May or may not detect a leak (40% probability)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detection completed")
    })
    @PostMapping("/run-detection")
    public ResponseEntity<Map<String, Object>> runDetection() {
        log.info("Manual detection triggered");

        Optional<LeakDTO> detectedLeak = satelliteScanScheduler.runSatelliteScan();

        Map<String, Object> response = new HashMap<>();

        if (detectedLeak.isPresent()) {
            response.put("leakDetected", true);
            response.put("message", "Methane leak detected!");
            response.put("leak", detectedLeak.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("leakDetected", false);
            response.put("message", "No methane leaks detected in this scan.");
            return ResponseEntity.ok(response);
        }
    }
}
