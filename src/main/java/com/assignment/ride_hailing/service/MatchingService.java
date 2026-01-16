package com.assignment.ride_hailing.service;

import com.assignment.ride_hailing.entity.Ride;

public interface MatchingService {

    Ride assignDriver(Long ride);
}
