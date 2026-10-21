package com.orvba.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.orvba.app.model.Mechanic;
import com.orvba.app.model.MechanicRegistrationRequest;
import com.orvba.app.service.MechanicService;

@RestController
@RequestMapping("/mechanics")
public class MechanicController {

    @Autowired
    private MechanicService mechanicService;

    @PostMapping("/register")
    public ResponseEntity<Mechanic> registerMechanic(
            @RequestBody MechanicRegistrationRequest request) {

        Mechanic savedMechanic = mechanicService.registerMechanic(request);

        return ResponseEntity.ok(savedMechanic);
    }

    @PutMapping("/availability")
    public ResponseEntity<Mechanic> updateAvailability(
            @RequestParam boolean available) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic mechanic = mechanicService.updateAvailability(username, available);

        if (mechanic == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(mechanic);
    }
@GetMapping("/me")
public ResponseEntity<Mechanic> getMyProfile() {

    String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    Mechanic mechanic = mechanicService.getMechanicByUsername(username);

    if (mechanic == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(mechanic);
}
}