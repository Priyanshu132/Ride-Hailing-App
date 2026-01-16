package com.assignment.ride_hailing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DriverDecisionRequest {

    private Long rideId;
    private boolean accepted;
}
