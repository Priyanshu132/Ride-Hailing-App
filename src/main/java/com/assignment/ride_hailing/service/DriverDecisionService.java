package com.assignment.ride_hailing.service;

import com.assignment.ride_hailing.dto.DriverDecisionRequest;
import com.assignment.ride_hailing.entity.Ride;

public interface DriverDecisionService {

    Ride respond(Long rideId, DriverDecisionRequest accepted);
}
