package com.orvba.app.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import com.orvba.app.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiry}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh.expiry}")
    private long refreshTokenExpiry;

  public String generateAccessToken(User user) {
    return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("role", user.getRole().name()) // ✅ still added here
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
}


    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public String extractRole(String token) { // ✅ helper to read role if needed
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody().get("role", String.class);
    }
}
