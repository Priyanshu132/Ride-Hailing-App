package com.assignment.ride_hailing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StartRideRequest {

    private Long rideId;
    private Integer otp;
}
