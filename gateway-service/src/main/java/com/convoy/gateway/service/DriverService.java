package com.convoy.gateway.service;

import com.convoy.gateway.dto.DriverDTO;
import com.convoy.gateway.dto.DriverLocationDTO;
import com.convoy.gateway.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }
    
    public DriverDTO getDriver(String id) {
        return driverRepository.findById(id);
    }
    
    public List<DriverLocationDTO> getNearbyDrivers(double latitude, double longitude, int radiusKm) {
        return driverRepository.findNearby(latitude, longitude, radiusKm);
    }
}
