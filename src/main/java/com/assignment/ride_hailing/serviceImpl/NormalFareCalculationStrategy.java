package com.assignment.ride_hailing.serviceImpl;

import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.service.FareCalculationStrategy;
import org.springframework.stereotype.Service;

@Service("NORMAL")
public class NormalFareCalculationStrategy implements FareCalculationStrategy {

    @Override
    public boolean supports(Ride ride) {
        return ride.getSurgeMultiplier() <= 1.0;
    }

    @Override
    public double calculateFare(Ride ride) {
        return 100;
    }
}
