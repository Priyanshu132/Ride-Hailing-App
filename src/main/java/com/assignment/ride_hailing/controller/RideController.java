package com.assignment.ride_hailing.controller;

import com.assignment.ride_hailing.dto.BaseResponse;
import com.assignment.ride_hailing.dto.RideRequest;
import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<BaseResponse> createRide(@Valid @RequestBody RideRequest request,
                                                   @AuthenticationPrincipal Long riderId, @RequestHeader("Idempotency-Key") String key) {
        request.setRiderId(riderId);
        Ride ride = rideService.createRide(request,key);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse("Ride Created",ride.getId()));
    }

    @PostMapping("/abort/{id}")
    public ResponseEntity<BaseResponse> abortRide(@PathVariable Long id,@AuthenticationPrincipal Long riderId) throws IllegalAccessException {
        Ride ride = rideService.abortRide(riderId,id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Ride Cancelled",ride.getId()));
    }

    @PostMapping("/assign/{rideId}")
    public ResponseEntity<BaseResponse> assignDriver(@PathVariable Long rideId,@AuthenticationPrincipal Long riderId) throws IllegalAccessException {
        Ride ride = rideService.assignDriver(rideId,riderId);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Driver Requested",ride.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRide(@PathVariable Long id) {
        Ride ride = rideService.getRide(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Ride Fetched",ride.getId()));

    }
}
