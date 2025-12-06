package com.azercosmos.back.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leak_coordinates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeakCoordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leak_id", nullable = false)
    @JsonIgnore
    private Leak leak;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;
}
