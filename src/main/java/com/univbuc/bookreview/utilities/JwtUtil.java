package com.univbuc.bookreview.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private Algorithm algorithm = Algorithm.HMAC256("12345");

    public String generateToken(String email) {
        long expirationTime = 3600000; // 1 hour in milliseconds
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    // Method to validate and parse the JWT token
    public String validateTokenAndGetEmail(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }
}
