package com.univbuc.bookreview.controllers;

import com.univbuc.bookreview.dto.AuthResponse;
import com.univbuc.bookreview.dto.UserLoginDto;
import com.univbuc.bookreview.dto.UserRegistrationResponseDto;
import com.univbuc.bookreview.utilities.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.dto.UserRegistrationDto;
import com.univbuc.bookreview.services.UserService;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDto> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        User user = new User(registrationDto.getUsername(), registrationDto.getEmail(), registrationDto.getPassword());
        User registeredUser = userService.registerUser(user);

        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto(
                registeredUser.getUsername(),
                registeredUser.getEmail()
        );

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto) {
        try {
            String token = userService.authenticate(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getUserStatus(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }

        boolean isLoggedIn = jwtUtil.isLoggedIn(token);

        if (!isLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        boolean isAdmin = jwtUtil.isAdmin(token);

        String status = "Logged In: " + isLoggedIn + ", Is Admin: " + isAdmin;
        return ResponseEntity.ok(status);
    }
}

