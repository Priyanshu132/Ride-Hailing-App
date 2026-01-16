package com.assignment.ride_hailing.serviceImpl;

import com.assignment.ride_hailing.dto.RideRequest;
import com.assignment.ride_hailing.dto.StartRideRequest;
import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.enums.RideStatus;
import com.assignment.ride_hailing.exceptions.DuplicateRequestException;
import com.assignment.ride_hailing.exceptions.OtpVerificationFailedException;
import com.assignment.ride_hailing.exceptions.RideNotFoundException;
import com.assignment.ride_hailing.factory.FareCalculationFactory;
import com.assignment.ride_hailing.repository.RideRepository;
import com.assignment.ride_hailing.service.FareCalculationStrategy;
import com.assignment.ride_hailing.service.MatchingService;
import com.assignment.ride_hailing.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import static com.assignment.ride_hailing.constant.ApplicationConstants.*;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RedisTemplate<String, String> redis;
    private final RideRepository rideRepository;
    private final MatchingService matchingService;
    private final FareCalculationFactory fareCalculationFactory;

    @Override
    public Ride createRide(RideRequest request,String IdempotencyKey) {

        // Check unique request
        if (redis.hasKey(REDIS_IDEMPOTENCY_KEY+request.getRiderId())) {
           throw new DuplicateRequestException();
        }else
            redis.opsForValue().set(REDIS_IDEMPOTENCY_KEY+request.getRiderId(), String.valueOf(request.getRiderId()));

        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setDestinationLatitude(request.getDestinationLatitude());
        ride.setDestinationLongitude(request.getDestinationLongitude());
        ride.setStatus(RideStatus.REQUESTED);

        // Apply surge
        double surge = new Random().nextDouble(1.0,2.5);
        ride.setSurgeMultiplier(surge);

        ride.setOtp(new SecureRandom().nextInt(9000) + 1000);
        Ride saved = rideRepository.save(ride);
        matchingService.assignDriver(saved.getId());

        return saved;
    }

    @Override
    public Ride startRide(StartRideRequest request, Long driverId) {
        Ride ride = getRide(request.getRideId());

        if(!Objects.equals(request.getOtp(), ride.getOtp())){
            throw new OtpVerificationFailedException("OTP did not matched!");
        }

        ride.setStatus(RideStatus.ON_TRIP);
        rideRepository.save(ride);
        return ride;
    }

    @Override
    public Ride getRide(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));
    }

    @Override
    public Ride endTrip(Long id,Long driverId) throws IllegalAccessException {
        Ride ride = getRide(id);

        if (!ride.getDriverId().equals(driverId) || ride.getStatus() != RideStatus.ON_TRIP) {
            throw new IllegalAccessException("Not your ride");
        }

        ride.setStatus(RideStatus.COMPLETED);
        redis.delete(REDIS_IDEMPOTENCY_KEY+ride.getRiderId());
        redis.delete(REDIS_ON_RIDE_DRIVER_KEY+ride.getDriverId());

        // used factory and strategy pattern here
        FareCalculationStrategy strategy = fareCalculationFactory.getFareCalculationStrategy(ride);
        ride.setFare(strategy.calculateFare(ride));
        return rideRepository.save(ride);
    }

    @Override
    public Ride abortRide(Long riderId, Long id) throws IllegalAccessException {
        Ride ride = getRide(id);

        if(ride.getStatus().equals(RideStatus.CANCELLED) || ride.getStatus().equals(RideStatus.COMPLETED))
            throw new IllegalAccessException("Ride already Completed/Cancelled");

        redis.delete(REDIS_IDEMPOTENCY_KEY+ride.getRiderId());
        redis.delete(REDIS_ASSIGN_DRIVER_KEY+ride.getRiderId());
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
        return ride;
    }

    @Override
    public Ride assignDriver(Long rideId, Long riderId) {
        boolean isNewRequestToAssignDriver = !redis.hasKey(REDIS_ASSIGN_DRIVER_KEY+riderId);
        if(isNewRequestToAssignDriver){
            Ride ride = getRide(rideId);
            if(ride.getStatus().equals(RideStatus.REQUESTED)){
                return matchingService.assignDriver(ride.getId());
            }
            return ride;
        }
        else
            throw new DuplicateRequestException();
    }
}
