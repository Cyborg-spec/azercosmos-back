package com.azercosmos.back.controller;

import com.azercosmos.back.dto.LeakDTO;
import com.azercosmos.back.enums.LeakStatus;
import com.azercosmos.back.scheduler.SatelliteScanScheduler;
import com.azercosmos.back.service.LeakService;
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
public class LeakController {

    private final LeakService leakService;
    private final SatelliteScanScheduler satelliteScanScheduler;

    @GetMapping
    public ResponseEntity<List<LeakDTO>> getAllLeaks() {
        log.info("Fetching all leaks");
        List<LeakDTO> leaks = leakService.getAllLeaks();
        return ResponseEntity.ok(leaks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeakDTO> getLeakById(@PathVariable String id) {
        log.info("Fetching leak: {}", id);
        return leakService.getLeakById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LeakDTO> createLeak(@Valid @RequestBody LeakDTO leakDTO) {
        log.info("Creating new leak at: {}", leakDTO.getLocationName());
        LeakDTO created = leakService.createLeak(leakDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LeakDTO> updateLeakStatus(
            @PathVariable String id,
            @RequestParam LeakStatus status) {
        log.info("Updating leak {} status to {}", id, status);
        return leakService.updateLeakStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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
