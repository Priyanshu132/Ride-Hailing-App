package com.assignment.ride_hailing.factory;

import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.service.FareCalculationStrategy;
import com.assignment.ride_hailing.serviceImpl.NormalFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FareCalculationFactory {

    private final List<FareCalculationStrategy> strategies;

    public FareCalculationStrategy getFareCalculationStrategy(Ride ride){

        return strategies.stream()
                .filter(strategy -> strategy.supports(ride))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No fare strategy found"));
    }
}
