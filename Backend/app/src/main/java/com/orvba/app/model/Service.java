package com.orvba.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    private String serviceName;

    private Double standardPrice;

    private Double maxAllowedPrice;

    private Double diagnosisFee;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public Double getMaxAllowedPrice() {
        return maxAllowedPrice;
    }

    public void setMaxAllowedPrice(Double maxAllowedPrice) {
        this.maxAllowedPrice = maxAllowedPrice;
    }

    public Double getDiagnosisFee() {
        return diagnosisFee;
    }

    public void setDiagnosisFee(Double diagnosisFee) {
        this.diagnosisFee = diagnosisFee;
    }
}