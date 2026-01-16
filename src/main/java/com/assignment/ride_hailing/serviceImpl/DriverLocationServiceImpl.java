package com.assignment.ride_hailing.serviceImpl;

import com.assignment.ride_hailing.constant.ApplicationConstants;
import com.assignment.ride_hailing.service.DriverLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.assignment.ride_hailing.constant.ApplicationConstants.REDIS_DRIVER_KEY;
import static com.assignment.ride_hailing.constant.ApplicationConstants.REDIS_DRIVER_LAST_SEEN_KEY;

@Service
@RequiredArgsConstructor
public class DriverLocationServiceImpl implements DriverLocationService {


    private final RedisTemplate<String, String> redis;

    @Override
    public void updateLocation(Long driverId, double lat, double lng) {
        redis.opsForGeo().add(REDIS_DRIVER_KEY, new Point(lng, lat), String.valueOf(driverId));
        redis.opsForZSet().add(
                REDIS_DRIVER_LAST_SEEN_KEY + driverId,
                String.valueOf(driverId),
                System.currentTimeMillis()
        );
    }

    @Override
    public List<Long> findNearbyDrivers(double lat, double lng, int radiusKm) {
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redis.opsForGeo().radius(
                        REDIS_DRIVER_KEY,
                        new Circle(
                                new Point(lng, lat),
                                new Distance(radiusKm, Metrics.KILOMETERS)
                        )
                );

        if (results == null) {
            return List.of();
        }

        return results.getContent()
                .stream()
                .map(r -> Long.parseLong(r.getContent().getName()))
                .toList();
    }
}
