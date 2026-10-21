
package com.orvba.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orvba.app.repository.ServiceRepository;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<com.orvba.app.model.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public com.orvba.app.model.Service addService(
            com.orvba.app.model.Service service) {

        validateService(service);
        return serviceRepository.save(service);
    }

    public com.orvba.app.model.Service updateService(
            Long serviceId,
            com.orvba.app.model.Service updated) {

        com.orvba.app.model.Service existing =
                serviceRepository.findById(serviceId).orElse(null);

        if (existing == null) {
            return null;
        }

        validateService(updated);

        existing.setServiceName(updated.getServiceName());
        existing.setStandardPrice(updated.getStandardPrice());
        existing.setMaxAllowedPrice(updated.getMaxAllowedPrice());
        existing.setDiagnosisFee(updated.getDiagnosisFee());

        return serviceRepository.save(existing);
    }

    private void validateService(
            com.orvba.app.model.Service service) {

        if (service.getServiceName() == null
                || service.getServiceName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Service name is required");
        }

        if (service.getStandardPrice() == null
                || service.getMaxAllowedPrice() == null
                || service.getDiagnosisFee() == null) {
            throw new IllegalArgumentException(
                    "Standard price, maximum price and diagnosis fee are required");
        }

        if (service.getStandardPrice() < 0
                || service.getMaxAllowedPrice()
                        < service.getStandardPrice()
                || service.getDiagnosisFee() < 0) {
            throw new IllegalArgumentException(
                    "Prices must be non-negative, and maximum price "
                    + "must be at least the standard price");
        }
    }
}
