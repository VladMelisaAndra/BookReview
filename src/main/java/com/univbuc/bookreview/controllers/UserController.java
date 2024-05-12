package com.univbuc.bookreview.controllers;

import com.univbuc.bookreview.domain.security.User;
import com.univbuc.bookreview.repositories.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(String username, String password, RedirectAttributes redirectAttributes) {
        if (userRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Username already exists!");
            return "redirect:/register";
        }
        if (!username.matches("^[a-zA-Z0-9_]{4,20}$")) {
            redirectAttributes.addFlashAttribute("error", "Invalid username!");
            return "redirect:/register";
        }
        if (!password.matches("^.{6,30}$")) {
            redirectAttributes.addFlashAttribute("error", "Password must be 6-30 characters long!");
            return "redirect:/register";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);

        redirectAttributes.addFlashAttribute("success", "Registration successful!");
        return "redirect:/login";
    }


}
