package com.convoy.gateway.repository;

import com.convoy.gateway.dto.DriverDTO;
import com.convoy.gateway.dto.DriverLocationDTO;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DriverRepository {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public DriverRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public DriverDTO findById(String driverId) {
        Point location = redisTemplate.opsForGeo().position("drivers",driverId).getFirst();

        if (location == null) return null;

        return new DriverDTO(driverId, location.getX(), location.getY());
    }
    
    public List<DriverLocationDTO> findNearby(double latitude, double longitude, int radiusKm) {
        Point center = new Point(longitude, latitude);

        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);

        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo()
                .search("drivers", GeoReference.fromCoordinate(center),distance,args);

        if (results == null) {
            return Collections.emptyList();
        }

        return results.getContent().stream()
                .map(geoResult -> {
                    RedisGeoCommands.GeoLocation<Object> location = geoResult.getContent();

                    String driverId = location.getName().toString();

                    double driverLongitude = location.getPoint().getX();
                    double driverLatitude=location.getPoint().getY();

                    double driverDistance = geoResult.getDistance().getValue();

                    return new DriverLocationDTO(driverId,driverLongitude,driverLatitude,driverDistance);
                })
                .collect(Collectors.toList());
    }
}
