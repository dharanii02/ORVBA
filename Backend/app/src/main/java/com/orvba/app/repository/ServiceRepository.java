package com.orvba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvba.app.model.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}