package com.assignment.ride_hailing.serviceImpl;

import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.service.FareCalculationStrategy;
import org.springframework.stereotype.Service;

@Service("SURGE")
public class SurgeFareCalculationStrategy implements FareCalculationStrategy {

    @Override
    public boolean supports(Ride ride) {
        return ride.getSurgeMultiplier() > 1.0;
    }

    @Override
    public double calculateFare(Ride ride) {
        double baseFare = 100;
        return Math.round(baseFare * ride.getSurgeMultiplier());
    }
}
