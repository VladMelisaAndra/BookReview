package com.univbuc.bookreview.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.auth0.jwt.exceptions.TokenExpiredException;

import java.util.Date;

@Component
public class JwtUtil {
    @Autowired
    private UserRepository userRepository;

    private Algorithm algorithm = Algorithm.HMAC256("12345");

    public String generateToken(String email, Long userId) {
        long expirationTime = 3600000; // 1 hour in milliseconds
        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userId)
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

    public boolean isLoggedIn(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token); // Verifies the token's signature and expiration
            return true; // Token is valid
        } catch (TokenExpiredException e) {
            return false; // expired
        } catch (JWTVerificationException e) {
            return false; // Token is invalid
        }
    }

    public boolean isAdmin(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        String email = jwt.getSubject();

        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user.getRole().getRoleName().equals("ADMIN");
        }
        return false;
    }

    public Long extractUserId(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        Long userId = jwt.getClaim("userId").asLong();
        return userId != null ? userId : null;
    }

    public boolean isTokenValid(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token); // throw an exception if the token is invalid or expired
            return true; // The token is valid
        } catch (JWTVerificationException e) {
            return false; // The token is invalid or expired
        }
    }

}
