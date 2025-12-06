package com.azercosmos.back.entity;

import com.azercosmos.back.enums.LeakStatus;
import com.azercosmos.back.enums.Severity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leaks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leak {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeakStatus status;

    @Column(name = "detected_by", nullable = false)
    private String detectedBy;

    @Column(name = "revenue_loss")
    private Double revenueLoss;

    @OneToMany(mappedBy = "leak", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<LeakCoordinate> coordinates = new ArrayList<>();

    public void addCoordinate(LeakCoordinate coordinate) {
        coordinates.add(coordinate);
        coordinate.setLeak(this);
    }

    public void removeCoordinate(LeakCoordinate coordinate) {
        coordinates.remove(coordinate);
        coordinate.setLeak(null);
    }
}
