
package com.orvba.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvba.app.model.ServiceRequest;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

    // Existing method: requests belonging to a user
    List<ServiceRequest> findByUser_IdOrderByRequestIdDesc(Long userId);

    // New method: requests assigned to a mechanic
    List<ServiceRequest> findByMechanic_MechanicIdOrderByRequestIdDesc(
            Long mechanicId);
}
