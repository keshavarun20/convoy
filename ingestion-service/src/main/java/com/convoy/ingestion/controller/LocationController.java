package com.convoy.ingestion.controller;

import com.convoy.ingestion.dto.LocationResponse;
import com.convoy.ingestion.dto.PingRequest;
import com.convoy.ingestion.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationResponse> receivePing (@RequestBody PingRequest pingRequest){
        locationService.forwardPing(pingRequest);
        return ResponseEntity.status(202)
                .body(new LocationResponse(true, "Location accepted"));
    }
}
