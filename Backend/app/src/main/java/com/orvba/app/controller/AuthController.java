package com.orvba.app.controller;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orvba.app.model.Role;
import com.orvba.app.model.User;
import com.orvba.app.repository.UserRepository;
import com.orvba.app.security.JwtUtil;
import com.orvba.app.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

     @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User Created Successfully");
    }
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User loginUser) {

    User user = userRepository.findByUsername(loginUser.getUsername());

    if (user != null
            && passwordEncoder.matches(
                    loginUser.getPassword(),
                    user.getPassword()
            )) {

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        Map<String, String> response = new HashMap<>();

        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body("Invalid username or password");
}
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> req) {
    String refreshToken = req.get("refreshToken");

    try {
        String username = jwtUtil.extractUsername(refreshToken);

        // ✅ FIX (convert username → User)
        User user = userRepository.findByUsername(username);

        // Now this works because method expects User
        String newAccessToken = jwtUtil.generateAccessToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    } 
    catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}

}

