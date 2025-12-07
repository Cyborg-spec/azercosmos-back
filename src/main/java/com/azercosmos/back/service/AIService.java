package com.azercosmos.back.service;

import com.azercosmos.back.dto.LeakDTO;
import com.azercosmos.back.enums.LeakStatus;
import com.azercosmos.back.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class AIService {

    private static final Random random = new Random();

    // Onshore locations in Azerbaijan with their approximate coordinates
    private static final Object[][] LAND_LOCATIONS = {
            // {name, centerLat, centerLon}
            { "Baku Oil Refinery Complex", 40.4093, 49.8671 },
            { "Sangachal Terminal", 40.1833, 49.4667 },
            { "Heydar Aliyev Refinery", 40.3456, 49.8234 },
            { "Sumgait Industrial Zone", 40.5897, 49.6317 },
            { "Shirvan Oil Fields", 39.9333, 48.9167 },
            { "Neftchala Processing Plant", 39.3833, 49.2500 },
            { "Bibi-Heybat Oil Field", 40.3167, 49.8000 },
            { "Balakhany Oil Field", 40.4333, 49.9333 },
            { "Surakhany Gas Facility", 40.4167, 50.0167 },
            { "Gobustan Gas Fields", 40.0833, 49.4167 },
            { "Garadagh Industrial Zone", 40.3500, 49.9667 },
            { "Lokbatan Oil Field", 40.3333, 49.7500 }
    };

    private static final String[] DETECTORS = {
            "Sentinel-5P",
            "GHGSat",
            "TROPOMI",
            "MethaneSat"
    };

    public void downloadSatelliteImages() {
        log.info("Downloading satellite images from orbit...");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Satellite images downloaded successfully");
    }

    public Optional<LeakDTO> runDetection() {
        log.info("Running AI model on satellite imagery...");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean leakDetected = random.nextInt(100) < 40;

        if (!leakDetected) {
            log.info("No methane leaks detected in current scan");
            return Optional.empty();
        }

        log.warn("Methane leak detected! Generating alert...");
        LeakDTO leak = generateMockLeak();
        return Optional.of(leak);
    }

    private LeakDTO generateMockLeak() {
        // Pick a random land location
        Object[] location = LAND_LOCATIONS[random.nextInt(LAND_LOCATIONS.length)];
        String locationName = (String) location[0];
        double baseLat = (Double) location[1];
        double baseLon = (Double) location[2];

        // Add small random offset to base coordinates
        baseLat += (random.nextDouble() - 0.5) * 0.02;
        baseLon += (random.nextDouble() - 0.5) * 0.02;

        // Generate polygon (4-6 points)
        int numPoints = 4 + random.nextInt(3);
        List<double[]> coordinates = new ArrayList<>();

        // Polygon size (smaller for land facilities)
        double polygonSize = 0.005 + random.nextDouble() * 0.01;

        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double lat = baseLat + polygonSize * Math.cos(angle);
            double lon = baseLon + polygonSize * Math.sin(angle);
            coordinates.add(new double[] {
                    Math.round(lat * 10000.0) / 10000.0,
                    Math.round(lon * 10000.0) / 10000.0
            });
        }

        Severity severity;
        int severityRoll = random.nextInt(100);
        if (severityRoll < 50) {
            severity = Severity.LOW;
        } else if (severityRoll < 85) {
            severity = Severity.MEDIUM;
        } else {
            severity = Severity.HIGH;
        }

        double revenueLoss = switch (severity) {
            case LOW -> 1000 + random.nextDouble() * 4000;
            case MEDIUM -> 5000 + random.nextDouble() * 10000;
            case HIGH -> 10000 + random.nextDouble() * 20000;
        };

        return LeakDTO.builder()
                .date(LocalDate.now())
                .locationName(locationName)
                .coordinates(coordinates)
                .severity(severity)
                .status(LeakStatus.NEW)
                .detectedBy(DETECTORS[random.nextInt(DETECTORS.length)])
                .revenueLoss(Math.round(revenueLoss * 100.0) / 100.0)
                .build();
    }
}
