package com.assignment.ride_hailing.service;

import com.assignment.ride_hailing.entity.Ride;

public interface FareCalculationStrategy {

    boolean supports(Ride ride);
    double calculateFare(Ride ride);
}
