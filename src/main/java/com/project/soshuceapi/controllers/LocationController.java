package com.project.soshuceapi.controllers;

import com.project.soshuceapi.services.LocationService;
import com.project.soshuceapi.services.iservice.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private ILocationService locationService;

    @GetMapping("/provinces")
    public ResponseEntity<?> getAllProvinces() {
        try {
            return ResponseEntity.ok(locationService.getAllProvinces());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getAllDistricts(@RequestParam(value = "provinceId", required = false, defaultValue = "0")
                                                 int provinceId) {
        try {
            return ResponseEntity.ok(locationService.getAllDistrics(provinceId));
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

    @GetMapping("/province")
    public ResponseEntity<?> getProvinceById(@RequestParam(value = "id") int id) {
        try {
            return ResponseEntity.ok(locationService.getProvinceById(id));
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
