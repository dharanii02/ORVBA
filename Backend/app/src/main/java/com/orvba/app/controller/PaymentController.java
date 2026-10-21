
package com.orvba.app.controller;

import com.orvba.app.model.PaymentStatus;
import com.orvba.app.model.RequestStatus;
import com.orvba.app.model.ServiceRequest;
import com.orvba.app.repository.ServiceRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @PostMapping("/{requestId}")
    public ResponseEntity<?> makePayment(
            @PathVariable Long requestId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getUser() == null
                || !request.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403)
                    .body("You can only pay for your own request.");
        }

        boolean payableStatus =
                request.getStatus() == RequestStatus.COMPLETED
                || request.getStatus() == RequestStatus.CANCELLED;

        if (!payableStatus) {
            return ResponseEntity.badRequest()
                    .body("Payment is allowed only after completion or cancellation.");
        }

        if (request.getFinalAmount() == null
                || request.getFinalAmount() <= 0) {
            return ResponseEntity.badRequest()
                    .body("No valid amount is due for this request.");
        }

        if (request.getPaymentStatus() != PaymentStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body("Payment is not pending or has already been paid.");
        }

        request.setPaymentStatus(PaymentStatus.PAID);

        ServiceRequest updatedRequest =
                serviceRequestRepository.save(request);

        return ResponseEntity.ok(updatedRequest);
    }
}
