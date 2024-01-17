package com.univbuc.bookreview.services;

import com.univbuc.bookreview.models.Role;
import com.univbuc.bookreview.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.repositories.UserRepository;
import com.univbuc.bookreview.utilities.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleRepository roleRepository;

    public User registerUser(User user) {
        Role userRole = roleRepository.findByRoleName("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(userRole);
        return userRepository.save(user);
    }

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(email, user.getId());
        }
        throw new RuntimeException("Invalid credentials");
    }
}
