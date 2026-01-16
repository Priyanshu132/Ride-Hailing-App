package com.assignment.ride_hailing.service;

import java.util.List;

public interface DriverLocationService {

    List<Long> findNearbyDrivers(double lat, double lng, int radiusKm);

    void updateLocation(Long driverId, double lat, double lng);
}
