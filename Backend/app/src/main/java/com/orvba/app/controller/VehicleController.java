
package com.orvba.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orvba.app.model.Vehicle;
import com.orvba.app.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(
            @RequestBody Vehicle vehicle) {

        Vehicle savedVehicle = vehicleService.addVehicle(vehicle);

        return ResponseEntity.ok(savedVehicle);
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<List<Vehicle>> getMyVehicles() {

        List<Vehicle> vehicles = vehicleService.getMyVehicles();

        return ResponseEntity.ok(vehicles);
    }

    // Keep the existing endpoint for compatibility
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Vehicle>> getUserVehicles(
            @PathVariable Long userId) {

        String username = org.springframework.security.core.context
                .SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (!vehicleService.isUserOwner(userId, username)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                vehicleService.getVehiclesByUserId(userId));
    }
}
