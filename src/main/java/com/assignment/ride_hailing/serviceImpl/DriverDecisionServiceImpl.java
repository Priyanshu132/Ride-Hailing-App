package com.assignment.ride_hailing.serviceImpl;

import com.assignment.ride_hailing.dto.DriverDecisionRequest;
import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.enums.RideStatus;
import com.assignment.ride_hailing.exceptions.RideNotFoundException;
import com.assignment.ride_hailing.repository.RideRepository;
import com.assignment.ride_hailing.service.DriverDecisionService;
import com.assignment.ride_hailing.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.assignment.ride_hailing.constant.ApplicationConstants.REDIS_ON_RIDE_DRIVER_KEY;

@Service
@RequiredArgsConstructor
public class DriverDecisionServiceImpl implements DriverDecisionService {

    private final RideRepository rideRepository;
    private final MatchingService matchingService;
    private final RedisTemplate<String, String> redis;


    @Override
    @Transactional
    public Ride respond(Long driverId, DriverDecisionRequest request) {
        Ride ride = rideRepository.findById(request.getRideId()).orElseThrow(() -> new RideNotFoundException(request.getRideId()));

        if (ride.getStatus() != RideStatus.DRIVER_ASSIGNED || !Objects.equals(ride.getDriverId(), driverId)) {
            throw new IllegalStateException("Invalid ride state");
        }

        if (request.isAccepted()) {
            redis.delete("driver_offer:" + request.getRideId());
            ride.setStatus(RideStatus.DRIVER_ACCEPTED);
            redis.opsForValue().set(REDIS_ON_RIDE_DRIVER_KEY+driverId,"1");
            return rideRepository.save(ride);
        } else {
            // decline requested
            ride.setDriverId(null);
            ride.setStatus(RideStatus.REQUESTED);
            rideRepository.save(ride);
            return matchingService.assignDriver(request.getRideId());
        }
    }
}
