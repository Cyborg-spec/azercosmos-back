package com.azercosmos.back.dto;

import com.azercosmos.back.enums.LeakStatus;
import com.azercosmos.back.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeakDTO {

    private String id;
    private LocalDate date;

    @NotBlank(message = "Location name is required")
    private String locationName;

    private List<double[]> coordinates;

    @NotNull(message = "Severity is required")
    private Severity severity;

    private LeakStatus status;
    private String detectedBy;
    private Double revenueLoss;
}
