package com.orvba.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.orvba.app.model.Mechanic;
import com.orvba.app.model.RequestStatus;
import com.orvba.app.model.ServiceRequest;
import com.orvba.app.service.MechanicService;
import com.orvba.app.service.ServiceRequestService;
import com.orvba.app.repository.ServiceRequestRepository;
import com.orvba.app.service.MechanicService;

@RestController
@RequestMapping("/requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private MechanicService mechanicService;

    @PostMapping
    public ResponseEntity<ServiceRequest> createRequest(
            @RequestBody ServiceRequest request) {

        ServiceRequest savedRequest = serviceRequestService.createRequest(request);

        return ResponseEntity.ok(savedRequest);
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<ServiceRequest> acceptRequest(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService.acceptRequest(requestId);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ServiceRequest> rejectRequest(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService.rejectRequest(requestId);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}/on-the-way")
    public ResponseEntity<ServiceRequest> markOnTheWay(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService.markOnTheWay(requestId);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(request);
    }


 @PutMapping("/{requestId}/arrived")
public ResponseEntity<ServiceRequest> markArrived(
        @PathVariable Long requestId) {

    ServiceRequest request =
            serviceRequestService.markArrived(requestId);

    if (request == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(request);
}


    @PutMapping("/{requestId}/completed")
    public ResponseEntity<ServiceRequest> markCompleted(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService.markCompleted(requestId);

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}/quotation/approve")
    public ResponseEntity<ServiceRequest> approveQuotation(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService.approveQuotation(requestId);

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}/quotation/reject")
    public ResponseEntity<ServiceRequest> rejectQuotation(
            @PathVariable Long requestId) {

        ServiceRequest request = serviceRequestService.rejectQuotation(requestId);

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}/quotation")
    public ResponseEntity<ServiceRequest> submitQuotation(
            @PathVariable Long requestId,
            @RequestBody ServiceRequest quotationData) {

        ServiceRequest request = serviceRequestService.submitQuotation(
                requestId,
                quotationData.getMechanicDiagnosis(),
                quotationData.getRepairQuotation());

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(request);
    }
    
@GetMapping("/my-requests")
public ResponseEntity<?> getMyRequests() {

    String role = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .map(authority -> authority.getAuthority())
            .findFirst()
            .orElse("");

    if (!role.equals("ROLE_USER")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(
            serviceRequestService.getRequestsForLoggedInUser()
    );
}
@GetMapping("/{requestId}")
public ResponseEntity<ServiceRequest> getRequestById(
        @PathVariable Long requestId) {

    ServiceRequest request =
            serviceRequestService.getRequestForLoggedInUser(requestId);

    if (request == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(request);
}

@GetMapping("/mechanic-requests")
public ResponseEntity<?> getMechanicRequests() {

    String role = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .map(authority -> authority.getAuthority())
            .findFirst()
            .orElse("");

    if (!role.equals("ROLE_MECHANIC")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(
            serviceRequestService.getRequestsForLoggedInMechanic());
}
@GetMapping("/admin-requests")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getAllRequestsForAdmin() {

    return ResponseEntity.ok(
            serviceRequestRepository.findAll()
    );
}
}