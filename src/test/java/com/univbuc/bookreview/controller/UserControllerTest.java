package com.univbuc.bookreview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.univbuc.bookreview.dto.UserLoginDto;
import com.univbuc.bookreview.dto.UserRegistrationDto;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.services.UserService;
import com.univbuc.bookreview.utilities.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegisterUser() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto("username", "email@example.com", "password");
        User user = new User("username", "email@example.com", "password");
        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("username")));
    }

    @Test
    void testLoginSuccess() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("email@example.com", "password");
        when(userService.authenticate(anyString(), anyString())).thenReturn("token");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt", is("token")));
    }

    @Test
    void testLoginFailure() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("email@example.com", "wrongpassword");
        when(userService.authenticate(anyString(), anyString())).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserStatus() throws Exception {
        String token = "Bearer validtoken";
        when(jwtUtil.isLoggedIn(anyString())).thenReturn(true);
        when(jwtUtil.isAdmin(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/users/status")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Logged In: true, Is Admin: false")));
    }
}
