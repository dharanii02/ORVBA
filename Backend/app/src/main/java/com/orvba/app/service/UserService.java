package com.orvba.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.orvba.app.model.Role;
import com.orvba.app.model.User;
import com.orvba.app.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public void createAdmin(String username, String password) {

        User admin = new User();

        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }
}