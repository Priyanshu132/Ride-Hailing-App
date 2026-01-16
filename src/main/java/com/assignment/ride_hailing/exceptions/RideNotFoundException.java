package com.assignment.ride_hailing.exceptions;

import lombok.Getter;

public class RideNotFoundException extends RuntimeException{

    @Getter
    private final Long rideId;

    public RideNotFoundException(Long rideId) {
        super("Ride not found: " + rideId);
        this.rideId = rideId;
    }
}
