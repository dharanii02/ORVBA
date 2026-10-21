package com.orvba.app.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orvba.app.service.ServiceRequestService;
import com.orvba.app.service.ServiceService;

import com.orvba.app.model.Mechanic;
import com.orvba.app.model.ServiceRequest;
import com.orvba.app.repository.MechanicRepository;
import com.orvba.app.service.ServiceRequestService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private MechanicRepository mechanicRepository;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping("/mechanics/pending")
    public ResponseEntity<List<Mechanic>> getPendingMechanics() {

        List<Mechanic> mechanics =
        mechanicRepository.findByApprovedFalseAndRejectedFalse();

        return ResponseEntity.ok(mechanics);
    }

 @PutMapping("/mechanics/{mechanicId}/approve")
public ResponseEntity<String> approveMechanic(
        @PathVariable Long mechanicId) {

    Mechanic mechanic = mechanicRepository.findById(mechanicId)
            .orElse(null);

    if (mechanic == null) {
        return ResponseEntity.notFound().build();
    }

    mechanic.setApproved(true);
    mechanic.setRejected(false);
    mechanicRepository.save(mechanic);

    return ResponseEntity.ok("Mechanic approved successfully");
}

@PutMapping("/mechanics/{mechanicId}/reject")
public ResponseEntity<String> rejectMechanic(
        @PathVariable Long mechanicId) {

    Mechanic mechanic = mechanicRepository.findById(mechanicId)
            .orElse(null);

    if (mechanic == null) {
        return ResponseEntity.notFound().build();
    }

    mechanic.setApproved(false);
    mechanic.setRejected(true);
    mechanic.setAvailable(false);
    mechanicRepository.save(mechanic);

    return ResponseEntity.ok("Mechanic rejected successfully");
}

@PostMapping("/services")
public ResponseEntity<?> addService(
        @RequestBody com.orvba.app.model.Service service) {

    try {
        com.orvba.app.model.Service savedService =
                serviceService.addService(service);

        return ResponseEntity.ok(savedService);

    } catch (IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

    @PutMapping("/requests/{requestId}/approve-amount")
    public ResponseEntity<ServiceRequest> approveAmount(
            @PathVariable Long requestId,
            @RequestBody Double approvedRepairAmount) {

        ServiceRequest request = serviceRequestService
                .approveAdminReview(requestId, approvedRepairAmount);

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(request);
    }

    @PutMapping("/requests/{requestId}/reject-quotation")
    public ResponseEntity<?> rejectQuotation(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService
                .rejectAdminReview(requestId);

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body("Unable to reject quotation. Check the request status and diagnosis fee.");
        }

        return ResponseEntity.ok(request);
    }
    
@GetMapping("/services")
public ResponseEntity<List<com.orvba.app.model.Service>> getAllServices() {
    return ResponseEntity.ok(serviceService.getAllServices());
}

@PutMapping("/services/{serviceId}")
public ResponseEntity<?> updateService(
        @PathVariable Long serviceId,
        @RequestBody com.orvba.app.model.Service service) {

    try {
        com.orvba.app.model.Service updated =
                serviceService.updateService(serviceId, service);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);

    } catch (IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}


}