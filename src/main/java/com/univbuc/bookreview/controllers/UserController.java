package com.univbuc.bookreview.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.dto.UserRegistrationDto;
import com.univbuc.bookreview.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        User user = new User(registrationDto.getUsername(), registrationDto.getEmail(), registrationDto.getPassword());
        return ResponseEntity.ok(userService.registerUser(user));
    }
}

