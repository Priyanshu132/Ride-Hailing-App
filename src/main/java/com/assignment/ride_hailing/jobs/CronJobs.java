package com.assignment.ride_hailing.jobs;

import com.assignment.ride_hailing.entity.Ride;
import com.assignment.ride_hailing.enums.RideStatus;
import com.assignment.ride_hailing.repository.RideRepository;
import com.assignment.ride_hailing.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static com.assignment.ride_hailing.constant.ApplicationConstants.REDIS_DRIVER_KEY;
import static com.assignment.ride_hailing.constant.ApplicationConstants.REDIS_DRIVER_LAST_SEEN_KEY;

@RequiredArgsConstructor
@Component
public class CronJobs {

    private final RedisTemplate<String, String> redis;
    private final RideRepository rideRepository;
    private final MatchingService matchingService;

    // every 1 hours
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void cleanupStaleDrivers() {

        long cutoff = System.currentTimeMillis() - Duration.ofDays(2).toMillis();

        Set<String> staleDrivers = redis.opsForZSet().rangeByScore(REDIS_DRIVER_LAST_SEEN_KEY, 0,cutoff);

        for (String driverId : staleDrivers) {
            redis.opsForGeo().remove(REDIS_DRIVER_KEY, driverId);
            redis.opsForZSet().remove(REDIS_DRIVER_LAST_SEEN_KEY, driverId);
        }
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void handleTimeouts() {

        // find rides still waiting for driver response
        List<Ride> rides =
                rideRepository.findAllByStatus(RideStatus.DRIVER_ASSIGNED);

        for (Ride ride : rides) {
            String key = "driver_offer:" + ride.getId();

            if (Boolean.FALSE.equals(redis.hasKey(key))) {
                // timeout happened
                ride.setDriverId(null);
                ride.setStatus(RideStatus.REQUESTED);
                rideRepository.save(ride);
            }
        }
    }
}
