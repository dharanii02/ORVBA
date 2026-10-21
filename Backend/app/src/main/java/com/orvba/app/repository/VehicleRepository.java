package com.orvba.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvba.app.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByUser_Id(Long userId);
}