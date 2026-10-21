package com.orvba.app.model;

import jakarta.persistence.*;
import com.orvba.app.model.Mechanic;

@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;

    private String location;

    private Double latitude;

    private Double longitude;

    private String problemDescription;

    private String mechanicDiagnosis;

    private Double repairQuotation;

    private Double estimatedPrice;

    private Double diagnosisFee;

    private Double approvedRepairAmount;

    private Double finalAmount;

    private boolean adminReviewRequired = false;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    // Getters and Setters

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public String getMechanicDiagnosis() {
        return mechanicDiagnosis;
    }

    public void setMechanicDiagnosis(String mechanicDiagnosis) {
        this.mechanicDiagnosis = mechanicDiagnosis;
    }

    public Double getRepairQuotation() {
        return repairQuotation;
    }

    public void setRepairQuotation(Double repairQuotation) {
        this.repairQuotation = repairQuotation;
    }

    public Double getDiagnosisFee() {
        return diagnosisFee;
    }

    public void setDiagnosisFee(Double diagnosisFee) {
        this.diagnosisFee = diagnosisFee;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public boolean isAdminReviewRequired() {
        return adminReviewRequired;
    }

    public void setAdminReviewRequired(boolean adminReviewRequired) {
        this.adminReviewRequired = adminReviewRequired;
    }

    public Double getApprovedRepairAmount() {
        return approvedRepairAmount;
    }

    public void setApprovedRepairAmount(Double approvedRepairAmount) {
        this.approvedRepairAmount = approvedRepairAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}