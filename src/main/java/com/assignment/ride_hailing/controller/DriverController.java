package com.assignment.ride_hailing.controller;

import com.assignment.ride_hailing.dto.BaseResponse;
import com.assignment.ride_hailing.dto.DriverDecisionRequest;
import com.assignment.ride_hailing.dto.LocationRequest;
import com.assignment.ride_hailing.dto.StartRideRequest;
import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.service.DriverDecisionService;
import com.assignment.ride_hailing.service.DriverLocationService;
import com.assignment.ride_hailing.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverLocationService locationService;
    private final DriverDecisionService driverDecisionService;
    private final RideService rideService;

    @PostMapping("/location")
    public ResponseEntity<BaseResponse> updateLocation(@AuthenticationPrincipal Long id,
                                                       @RequestBody LocationRequest locationRequest) {
        locationService.updateLocation(id, locationRequest.getLatitude(), locationRequest.getLongitude());
        return ResponseEntity.ok(new BaseResponse("location updated Successfully"));
    }

    @PostMapping("/accept")
    public ResponseEntity<BaseResponse> driverDecision(@AuthenticationPrincipal Long id,
                               @RequestBody DriverDecisionRequest request) {
        Ride ride = driverDecisionService.respond(id, request);
        return ResponseEntity.ok(new BaseResponse("Driver Accepted the Ride",ride.getId()));
    }

    @PostMapping("/start")
    public ResponseEntity<BaseResponse> startRide(@AuthenticationPrincipal Long id,
                                                       @RequestBody StartRideRequest request) {
        Ride ride = rideService.startRide(request,id);
        return ResponseEntity.ok(new BaseResponse("Ride Started",ride.getId()));
    }

    @PostMapping("/ride/{id}/end")
    public ResponseEntity<BaseResponse> endTrip(@PathVariable(name = "id") Long rideId,
                        @AuthenticationPrincipal Long driverId) throws IllegalAccessException{
        Ride ride = rideService.endTrip(rideId,driverId);
        return ResponseEntity.ok(new BaseResponse("Ride Ended","Fare is : "+ride.getFare()));
    }
}
