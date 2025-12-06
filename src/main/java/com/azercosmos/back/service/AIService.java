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

    private static final String[] LOCATION_NAMES = {
            "Shah Deniz Field (Offshore)",
            "Baku Oil Refinery Complex",
            "Sangachal Terminal",
            "Neft Dashlari Platform",
            "Chirag Field (Offshore)",
            "Gunashli Field",
            "Heydar Aliyev Refinery",
            "West Absheron Gas Field"
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
        double baseLat = 39.5 + random.nextDouble() * 1.5;
        double baseLon = 49.5 + random.nextDouble() * 1.5;

        int numPoints = 4 + random.nextInt(3);
        List<double[]> coordinates = new ArrayList<>();

        double polygonSize = 0.02 + random.nextDouble() * 0.03;

        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double lat = baseLat + polygonSize * Math.cos(angle);
            double lon = baseLon + polygonSize * Math.sin(angle);
            coordinates.add(new double[] {
                    Math.round(lat * 1000.0) / 1000.0,
                    Math.round(lon * 1000.0) / 1000.0
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
                .locationName(LOCATION_NAMES[random.nextInt(LOCATION_NAMES.length)])
                .coordinates(coordinates)
                .severity(severity)
                .status(LeakStatus.NEW)
                .detectedBy(DETECTORS[random.nextInt(DETECTORS.length)])
                .revenueLoss(Math.round(revenueLoss * 100.0) / 100.0)
                .build();
    }
}
