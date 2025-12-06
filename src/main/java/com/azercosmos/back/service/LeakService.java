package com.azercosmos.back.service;

import com.azercosmos.back.dto.LeakDTO;
import com.azercosmos.back.entity.Leak;
import com.azercosmos.back.entity.LeakCoordinate;
import com.azercosmos.back.enums.LeakStatus;
import com.azercosmos.back.repository.LeakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeakService {

    private final LeakRepository leakRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<LeakDTO> getAllLeaks() {
        return leakRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<LeakDTO> getLeakById(String id) {
        return leakRepository.findById(id)
                .map(this::toDTO);
    }

    @Transactional
    public LeakDTO createLeak(LeakDTO dto) {
        Leak leak = toEntity(dto);

        if (leak.getId() == null || leak.getId().isBlank()) {
            leak.setId(generateLeakId());
        }

        if (leak.getDate() == null) {
            leak.setDate(LocalDate.now());
        }
        if (leak.getStatus() == null) {
            leak.setStatus(LeakStatus.NEW);
        }

        Leak saved = leakRepository.save(leak);
        LeakDTO result = toDTO(saved);

        broadcastLeak(result);

        log.info("Saved new leak: {} at {}", saved.getId(), saved.getLocationName());
        return result;
    }

    @Transactional
    public Optional<LeakDTO> updateLeakStatus(String id, LeakStatus newStatus) {
        return leakRepository.findById(id)
                .map(leak -> {
                    leak.setStatus(newStatus);
                    Leak saved = leakRepository.save(leak);
                    LeakDTO result = toDTO(saved);

                    // Broadcast status update to WebSocket subscribers
                    broadcastLeakUpdate(result);

                    log.info("Updated leak {} status to {}", id, newStatus);
                    return result;
                });
    }

    public void broadcastLeak(LeakDTO leak) {
        log.info("Broadcasting leak {} to WebSocket subscribers", leak.getId());
        messagingTemplate.convertAndSend("/topic/leaks", leak);
    }

    public void broadcastLeakUpdate(LeakDTO leak) {
        log.info("Broadcasting leak update {} to WebSocket subscribers", leak.getId());
        messagingTemplate.convertAndSend("/topic/leaks/updates", leak);
    }

    private String generateLeakId() {
        int year = Year.now().getValue();
        long count = leakRepository.count() + 1;
        return String.format("LK-%d-%03d", year, count);
    }

    private LeakDTO toDTO(Leak leak) {
        List<double[]> coordinates = leak.getCoordinates()
                .stream()
                .map(c -> new double[] { c.getLatitude(), c.getLongitude() })
                .collect(Collectors.toList());

        return LeakDTO.builder()
                .id(leak.getId())
                .date(leak.getDate())
                .locationName(leak.getLocationName())
                .coordinates(coordinates)
                .severity(leak.getSeverity())
                .status(leak.getStatus())
                .detectedBy(leak.getDetectedBy())
                .revenueLoss(leak.getRevenueLoss())
                .build();
    }

    private Leak toEntity(LeakDTO dto) {
        Leak leak = Leak.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .locationName(dto.getLocationName())
                .severity(dto.getSeverity())
                .status(dto.getStatus())
                .detectedBy(dto.getDetectedBy())
                .revenueLoss(dto.getRevenueLoss())
                .build();

        if (dto.getCoordinates() != null) {
            for (double[] coord : dto.getCoordinates()) {
                LeakCoordinate coordinate = LeakCoordinate.builder()
                        .latitude(coord[0])
                        .longitude(coord[1])
                        .build();
                leak.addCoordinate(coordinate);
            }
        }

        return leak;
    }
}
