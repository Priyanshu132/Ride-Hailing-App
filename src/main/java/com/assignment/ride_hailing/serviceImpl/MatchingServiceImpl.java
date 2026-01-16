package com.assignment.ride_hailing.serviceImpl;

import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.enums.RideStatus;
import com.assignment.ride_hailing.exceptions.NotFoundException;
import com.assignment.ride_hailing.exceptions.RideNotFoundException;
import com.assignment.ride_hailing.repository.RideRepository;
import com.assignment.ride_hailing.service.DriverLocationService;
import com.assignment.ride_hailing.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.annotations.NotFound;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.assignment.ride_hailing.constant.ApplicationConstants.*;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private static final int DRIVER_RESPONSE_TIMEOUT_SECONDS = 30;

    private final RideRepository rideRepository;
    private final DriverLocationService locationService;
    private final RedisTemplate<String, String> redis;


    @Override
    @Transactional
    public Ride assignDriver(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RideNotFoundException(rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            return ride;
        }
        redis.opsForValue().set(REDIS_ASSIGN_DRIVER_KEY+ride.getRiderId(),"1",30,TimeUnit.SECONDS);
        List<Long> drivers =
                locationService.findNearbyDrivers(
                        ride.getPickupLatitude(),
                        ride.getPickupLongitude(),
                        5
                ).stream().filter(dId->!redis.hasKey(REDIS_ON_RIDE_DRIVER_KEY+dId)).toList(); // removing already on trip drivers


        if (drivers.isEmpty()) {
            throw new NotFoundException("No drivers nearby");
        }

        Long driverId = drivers.getFirst();
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.DRIVER_ASSIGNED);

        rideRepository.save(ride);

        // driver App will be listening from here
        redis.opsForValue().set(
                "driver_offer:" + rideId,
                String.valueOf(driverId),
                DRIVER_RESPONSE_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
        );
        return ride;
    }
}
