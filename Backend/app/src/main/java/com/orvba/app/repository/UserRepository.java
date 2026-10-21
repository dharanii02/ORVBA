package com.orvba.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.orvba.app.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

