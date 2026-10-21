package com.orvba.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.orvba.app.repository.UserRepository;
import com.orvba.app.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

  

    @Override
    public void run(String... args) {

        if (userRepository.findByUsername("admin") == null) {

            userService.createAdmin(
                    "admin",
                    "admin123"
            );

            System.out.println("Default admin account created.");
        }

    }
}