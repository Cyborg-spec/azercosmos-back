package com.azercosmos.back.scheduler;

import com.azercosmos.back.dto.LeakDTO;
import com.azercosmos.back.service.AIService;
import com.azercosmos.back.service.LeakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SatelliteScanScheduler {

    private final AIService aiService;
    private final LeakService leakService;

    @Scheduled(cron = "0 0 */72 * * *")
    public void runScheduledSatelliteScan() {
        log.info("Scheduled satellite scan triggered");
        runSatelliteScan();
    }

    public Optional<LeakDTO> runSatelliteScan() {
        log.info("Starting satellite scan...");

        try {
            aiService.downloadSatelliteImages();

            Optional<LeakDTO> detectedLeak = aiService.runDetection();

            if (detectedLeak.isPresent()) {
                LeakDTO leak = detectedLeak.get();
                LeakDTO savedLeak = leakService.createLeak(leak);
                log.info("Leak detected and saved: {}", savedLeak.getId());
                return Optional.of(savedLeak);
            }

            log.info("Satellite scan complete - no leaks detected");
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error during satellite scan: {}", e.getMessage(), e);
            throw new RuntimeException("Satellite scan failed", e);
        }
    }
}
