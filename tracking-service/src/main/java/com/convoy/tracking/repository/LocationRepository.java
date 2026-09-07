package com.convoy.tracking.repository;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class LocationRepository {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public LocationRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveLocation(String driverId, double latitude, double longitude, long incomingTimestamp) {
        String timestampKey = "driver:timestamp:" + driverId;

        // Use ValueOperations helper
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        // 1. Fix Type Mismatch: Cast or convert the Object safely to a String/Long
        Object lastTimestampObj = valueOps.get(timestampKey);

        if (lastTimestampObj != null) {
            // Safe conversion whether it comes back as a String or a Number number
            long lastTimestamp = Long.parseLong(lastTimestampObj.toString());

            // 2. Discard out-of-order pings
            if (incomingTimestamp <= lastTimestamp) {
                System.out.println("Discarded out-of-order ping for driver: " + driverId);
                return;
            }
        }

        // 3. Fix Deprecation: Use Duration.ofHours(24) instead of long/TimeUnit parameters
        valueOps.set(timestampKey, String.valueOf(incomingTimestamp), Duration.ofHours(24));

        // 4. Save the new coordinates to the geo index
        Point location = new Point(longitude, latitude);
        redisTemplate.opsForGeo().add("drivers", location, driverId);
    }
}
