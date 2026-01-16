package com.assignment.ride_hailing.service;

import com.assignment.ride_hailing.dto.RideRequest;
import com.assignment.ride_hailing.dto.StartRideRequest;
import com.assignment.ride_hailing.entity.Ride;

public interface RideService {

    Ride createRide(RideRequest request,String IdempotencyKey);

    Ride startRide(StartRideRequest request,Long driverId);

    Ride getRide(Long id);

    Ride endTrip(Long id,Long driverId) throws IllegalAccessException;

    Ride abortRide(Long riderId, Long id) throws IllegalAccessException;

    Ride assignDriver(Long rideId, Long riderId) throws IllegalAccessException;
}
