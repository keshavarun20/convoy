package com.convoy.gateway.graphql;

import com.convoy.gateway.dto.DriverDTO;
import com.convoy.gateway.dto.DriverLocationDTO;
import com.convoy.gateway.service.DriverService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DriverResolver {
    
    private final DriverService driverService;
    
    public DriverResolver(DriverService driverService) {
        this.driverService = driverService;
    }
    
    @QueryMapping
    public DriverDTO driver(@Argument String id) {
        return driverService.getDriver(id);
    }
    
    @QueryMapping
    public List<DriverLocationDTO> driversNearby(
            @Argument double latitude,
            @Argument double longitude,
            @Argument int radiusKm) {
        return driverService.getNearbyDrivers(latitude,longitude,radiusKm);
    }
}
