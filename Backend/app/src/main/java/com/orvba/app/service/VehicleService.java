
package com.orvba.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.orvba.app.model.User;
import com.orvba.app.model.Vehicle;
import com.orvba.app.repository.UserRepository;
import com.orvba.app.repository.VehicleRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    public Vehicle addVehicle(Vehicle vehicle) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("Authenticated user not found");
        }

        vehicle.setUser(user);

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getMyVehicles() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("Authenticated user not found");
        }

        return vehicleRepository.findByUser_Id(user.getId());
    }

    public List<Vehicle> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUser_Id(userId);
    }

    public boolean isUserOwner(Long userId, String username) {

        User user = userRepository.findByUsername(username);

        return user != null && user.getId().equals(userId);
    }
}
