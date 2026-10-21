package com.orvba.app.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.orvba.app.model.Mechanic;
import com.orvba.app.model.MechanicRegistrationRequest;
import com.orvba.app.model.Role;
import com.orvba.app.model.User;
import com.orvba.app.repository.MechanicRepository;
import com.orvba.app.repository.UserRepository;
import com.orvba.app.util.DistanceUtil;

@Service
public class MechanicService {

        @Autowired
        private MechanicRepository mechanicRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        public Mechanic registerMechanic(
                        MechanicRegistrationRequest request) {

                // Create login account
                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole(Role.MECHANIC);
                userRepository.save(user);

                // Create mechanic profile
                Mechanic mechanic = new Mechanic();
                mechanic.setUser(user);
                mechanic.setName(request.getName());
                mechanic.setPhone(request.getPhone());
                mechanic.setEmail(request.getEmail());
                mechanic.setAddress(request.getAddress());
                mechanic.setSpecialization(request.getSpecialization());
                mechanic.setExperience(request.getExperience());
                mechanic.setIdProof(request.getIdProof());
                mechanic.setIdProofNumber(request.getIdProofNumber());
                mechanic.setLatitude(request.getLatitude());
                mechanic.setLongitude(request.getLongitude());

                // New mechanic must be approved by admin
                mechanic.setApproved(false);

                // New mechanic is initially unavailable
                mechanic.setAvailable(false);

                return mechanicRepository.save(mechanic);
        }

        public List<Mechanic> getAvailableMechanics() {
                return mechanicRepository.findByApprovedTrueAndAvailableTrue();
        }

        public Mechanic findNearestMechanic(double userLatitude, double userLongitude) {

                List<Mechanic> mechanics = mechanicRepository.findByApprovedTrueAndAvailableTrue();

                return mechanics.stream()
                                .min(Comparator.comparingDouble(mechanic -> DistanceUtil.calculateDistance(
                                                userLatitude,
                                                userLongitude,
                                                mechanic.getLatitude(),
                                                mechanic.getLongitude())))
                                .orElse(null);
        }

        public Mechanic findNextNearestMechanic(
                        double userLatitude,
                        double userLongitude,
                        Long currentMechanicId) {

                List<Mechanic> mechanics = mechanicRepository.findByApprovedTrueAndAvailableTrue();

                return mechanics.stream()
                                .filter(mechanic -> !mechanic.getMechanicId().equals(currentMechanicId))
                                .min(Comparator.comparingDouble(mechanic -> DistanceUtil.calculateDistance(
                                                userLatitude,
                                                userLongitude,
                                                mechanic.getLatitude(),
                                                mechanic.getLongitude())))
                                .orElse(null);
        }

        public Mechanic getMechanicByUsername(String username) {
                return mechanicRepository.findByUser_Username(username);
        }

        public Mechanic updateAvailability(String username, boolean available) {

                Mechanic mechanic = mechanicRepository.findByUser_Username(username);

                if (mechanic == null) {
                        return null;
                }

                if (available && !mechanic.isApproved()) {
                        return null;
                }

                mechanic.setAvailable(available);

                return mechanicRepository.save(mechanic);
        }

}