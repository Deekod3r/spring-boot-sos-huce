package com.project.soshuceapi.controllers;

import com.project.soshuceapi.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/districts")
    public ResponseEntity<?> getAllDistricts() {
        try {
            return ResponseEntity.ok(locationService.getAllDistrics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getAllWards(@RequestParam(value = "districtId", required = false, defaultValue = "0")
                                             int districtId) {
        try {
            return ResponseEntity.ok(locationService.getAllWards(districtId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/district")
    public ResponseEntity<?> getDistrictById(@RequestParam(value = "id") int id) {
        try {
            return ResponseEntity.ok(locationService.getDistrictById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/ward")
    public ResponseEntity<?> getWardById(@RequestParam(value = "id") int id) {
        try {
            return ResponseEntity.ok(locationService.getWardById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
