package com.orvba.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.orvba.app.model.Mechanic;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {

    List<Mechanic> findByApprovedFalseAndRejectedFalse();

    List<Mechanic> findByApprovedTrueAndAvailableTrue();

    Mechanic findByUser_Username(String username);
}