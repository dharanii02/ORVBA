package com.orvba.app.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.orvba.app.model.Mechanic;
import com.orvba.app.model.PaymentStatus;
import com.orvba.app.model.RequestStatus;
import com.orvba.app.model.ServiceRequest;
import com.orvba.app.repository.ServiceRepository;
import com.orvba.app.repository.ServiceRequestRepository;
import com.orvba.app.model.User;
import com.orvba.app.model.Vehicle;
import com.orvba.app.repository.UserRepository;
import com.orvba.app.repository.VehicleRepository;
import com.orvba.app.model.PaymentStatus;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceRepository serviceRepository;

 
public ServiceRequest createRequest(ServiceRequest request) {

    String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    // Get the user from the logged-in account
    User user = userRepository.findByUsername(username);

    if (user == null
            || request.getVehicle() == null
            || request.getVehicle().getVehicleId() == null
            || request.getService() == null
            || request.getService().getServiceId() == null
            || request.getLatitude() == null
            || request.getLongitude() == null) {
        return null;
    }

    // Verify that the selected vehicle belongs to this user
    Vehicle vehicle = vehicleRepository
            .findById(request.getVehicle().getVehicleId())
            .orElse(null);

    if (vehicle == null
            || vehicle.getUser() == null
            || !vehicle.getUser().getId().equals(user.getId())) {
        return null;
    }

    // Verify that the selected service exists
    com.orvba.app.model.Service service =
            serviceRepository.findById(
                    request.getService().getServiceId()
            ).orElse(null);

    if (service == null) {
        return null;
    }

    // Assign trusted database entities
    request.setUser(user);
    request.setVehicle(vehicle);
    request.setService(service);

    request.setEstimatedPrice(service.getStandardPrice());
    request.setDiagnosisFee(service.getDiagnosisFee());
    request.setStatus(RequestStatus.PENDING);

    // Find the nearest available mechanic
    Mechanic nearestMechanic = mechanicService.findNearestMechanic(
            request.getLatitude(),
            request.getLongitude()
    );

    if (nearestMechanic != null) {
        request.setMechanic(nearestMechanic);
        request.setStatus(RequestStatus.MECHANIC_ASSIGNED);
    }

    return serviceRequestRepository.save(request);
}

    public ServiceRequest acceptRequest(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getMechanic() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic loggedInMechanic = mechanicService.getMechanicByUsername(username);

        if (loggedInMechanic == null) {
            return null;
        }

        // Make sure the logged-in mechanic is the assigned mechanic
        if (!loggedInMechanic.getMechanicId()
                .equals(request.getMechanic().getMechanicId())) {
            return null;
        }

        if (request.getStatus() != RequestStatus.MECHANIC_ASSIGNED) {
            return null;
        }

        request.setStatus(RequestStatus.ACCEPTED);

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest rejectRequest(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getMechanic() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic loggedInMechanic = mechanicService.getMechanicByUsername(username);

        if (loggedInMechanic == null) {
            return null;
        }

        if (!loggedInMechanic.getMechanicId()
                .equals(request.getMechanic().getMechanicId())) {
            return null;
        }

        if (request.getStatus() != RequestStatus.MECHANIC_ASSIGNED) {
            return null;
        }

        Long currentMechanicId = request.getMechanic().getMechanicId();

        Mechanic nextMechanic = mechanicService.findNextNearestMechanic(
                request.getLatitude(),
                request.getLongitude(),
                currentMechanicId);

        if (nextMechanic != null) {
            request.setMechanic(nextMechanic);
            request.setStatus(RequestStatus.MECHANIC_ASSIGNED);
        } else {
            request.setMechanic(null);
            request.setStatus(RequestStatus.PENDING);
        }

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest markOnTheWay(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getMechanic() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic loggedInMechanic = mechanicService.getMechanicByUsername(username);

        if (loggedInMechanic == null) {
            return null;
        }

        if (!loggedInMechanic.getMechanicId()
                .equals(request.getMechanic().getMechanicId())) {
            return null;
        }

        if (request.getStatus() != RequestStatus.ACCEPTED) {
            return null;
        }

        request.setStatus(RequestStatus.ON_THE_WAY);

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest markArrived(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getMechanic() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic loggedInMechanic = mechanicService.getMechanicByUsername(username);

        if (loggedInMechanic == null) {
            return null;
        }

        if (!loggedInMechanic.getMechanicId()
                .equals(request.getMechanic().getMechanicId())) {
            return null;
        }

        if (request.getStatus() != RequestStatus.ON_THE_WAY) {
            return null;
        }

        request.setStatus(RequestStatus.ARRIVED);

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest submitQuotation(
            Long requestId,
            String mechanicDiagnosis,
            Double repairQuotation) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getMechanic() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic loggedInMechanic = mechanicService.getMechanicByUsername(username);

        if (loggedInMechanic == null) {
            return null;
        }

        if (!loggedInMechanic.getMechanicId()
                .equals(request.getMechanic().getMechanicId())) {
            return null;
        }

        // Quotation can be submitted only after mechanic has arrived
        if (request.getStatus() != RequestStatus.ARRIVED) {
            return null;
        }
        if (repairQuotation == null || repairQuotation < 0) {
            return null;
        }

        request.setMechanicDiagnosis(mechanicDiagnosis);
        request.setRepairQuotation(repairQuotation);

        Double maxAllowedPrice = request.getService().getMaxAllowedPrice();

        if (maxAllowedPrice == null) {
            return null;
        }

        if (repairQuotation > maxAllowedPrice) {
            request.setAdminReviewRequired(true);
            request.setStatus(RequestStatus.ADMIN_REVIEW);
        } else {
            request.setAdminReviewRequired(false);
            request.setStatus(RequestStatus.QUOTATION_PENDING);
        }

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest approveQuotation(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getUser() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (!request.getUser().getUsername().equals(username)) {
            return null;
        }

        // User can approve only when quotation is waiting for approval
        if (request.getStatus() != RequestStatus.QUOTATION_PENDING) {
            return null;
        }

        if (request.getApprovedRepairAmount() != null) {
            request.setRepairQuotation(request.getApprovedRepairAmount());
        }

        request.setStatus(RequestStatus.IN_PROGRESS);

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest rejectQuotation(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getUser() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (!request.getUser().getUsername().equals(username)) {
            return null;
        }

        // User can reject only when quotation is waiting for approval
        if (request.getStatus() != RequestStatus.QUOTATION_PENDING) {
            return null;
        }

        if (request.getDiagnosisFee() == null
                || request.getDiagnosisFee() <= 0) {
            return null;
        }

        request.setFinalAmount(request.getDiagnosisFee());
        request.setAdminReviewRequired(false);
        request.setPaymentStatus(PaymentStatus.PENDING);
        request.setStatus(RequestStatus.CANCELLED);

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest markCompleted(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getMechanic() == null) {
            return null;
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Mechanic loggedInMechanic = mechanicService.getMechanicByUsername(username);

        if (loggedInMechanic == null) {
            return null;
        }

        if (!loggedInMechanic.getMechanicId()
                .equals(request.getMechanic().getMechanicId())) {
            return null;
        }

        if (request.getStatus() != RequestStatus.IN_PROGRESS) {
            return null;
        }

        Double diagnosisFee = request.getDiagnosisFee();
        Double approvedAmount = request.getApprovedRepairAmount();

        if (approvedAmount == null) {
            approvedAmount = request.getRepairQuotation();
        }

        if (diagnosisFee == null || diagnosisFee < 0
                || approvedAmount == null || approvedAmount < 0) {
            return null;
        }

        Double maxAllowedPrice = request.getService().getMaxAllowedPrice();

        if (maxAllowedPrice == null || approvedAmount > maxAllowedPrice) {
            return null;
        }

        request.setAdminReviewRequired(false);
        request.setApprovedRepairAmount(approvedAmount);
        request.setFinalAmount(diagnosisFee + approvedAmount);
        request.setPaymentStatus(PaymentStatus.PENDING);
        request.setStatus(RequestStatus.COMPLETED);

        return serviceRequestRepository.save(request);

    }

    public ServiceRequest approveAdminReview(Long requestId, Double approvedRepairAmount) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getStatus() != RequestStatus.ADMIN_REVIEW) {
            return null;
        }

        if (approvedRepairAmount == null || approvedRepairAmount < 0) {
            return null;
        }

        Double maxAllowedPrice = request.getService().getMaxAllowedPrice();

        if (approvedRepairAmount > maxAllowedPrice) {
            return null;
        }

        request.setApprovedRepairAmount(approvedRepairAmount);
        request.setRepairQuotation(approvedRepairAmount);

        request.setAdminReviewRequired(false);
        request.setStatus(RequestStatus.QUOTATION_PENDING);

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest rejectAdminReview(Long requestId) {

        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElse(null);

        if (request == null) {
            return null;
        }

        if (request.getStatus() != RequestStatus.ADMIN_REVIEW) {
            return null;
        }

        if (request.getDiagnosisFee() == null
                || request.getDiagnosisFee() <= 0) {
            return null;
        }

        request.setFinalAmount(request.getDiagnosisFee());
        request.setAdminReviewRequired(false);
        request.setPaymentStatus(PaymentStatus.PENDING);
        request.setStatus(RequestStatus.CANCELLED);

        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> getRequestsForLoggedInUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return java.util.Collections.emptyList();
        }

        return serviceRequestRepository
                .findByUser_IdOrderByRequestIdDesc(user.getId());
    }

    
public ServiceRequest getRequestForLoggedInUser(Long requestId) {

    String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User user = userRepository.findByUsername(username);

    if (user == null) {
        return null;
    }

    ServiceRequest request = serviceRequestRepository
            .findById(requestId)
            .orElse(null);

    if (request == null || request.getUser() == null) {
        return null;
    }

    // Users can view only their own requests
    if (!request.getUser().getId().equals(user.getId())) {
        return null;
    }

    return request;
}

public List<ServiceRequest> getRequestsForLoggedInMechanic() {

    String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    Mechanic mechanic = mechanicService.getMechanicByUsername(username);

    if (mechanic == null) {
        return java.util.Collections.emptyList();
    }

    return serviceRequestRepository
            .findByMechanic_MechanicIdOrderByRequestIdDesc(
                    mechanic.getMechanicId());
}


}