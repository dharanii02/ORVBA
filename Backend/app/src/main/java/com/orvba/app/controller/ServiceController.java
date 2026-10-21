package com.orvba.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orvba.app.model.Service;
import com.orvba.app.model.ServiceRequest;
import com.orvba.app.repository.ServiceRepository;
import com.orvba.app.service.ServiceRequestService;

@RestController
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
private ServiceRequestService serviceRequestService;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {

        List<Service> services = serviceRepository.findAll();

        return ResponseEntity.ok(services);
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

}