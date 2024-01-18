package com.univbuc.bookreview.services;

import com.univbuc.bookreview.models.Role;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.repositories.RoleRepository;
import com.univbuc.bookreview.repositories.UserRepository;
import com.univbuc.bookreview.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        role = new Role();
        role.setId(1L);
        role.setRoleName("USER");
    }

    @Test
    void registerUser() {
        when(roleRepository.findByRoleName("USER")).thenReturn(role);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("encodedPassword", registeredUser.getPassword());
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyLong())).thenReturn("token");

        String token = userService.authenticate("test@example.com", "password");

        assertNotNull(token);
        assertEquals("token", token);
    }

    @Test
    void authenticate_InvalidCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.authenticate("invalid@example.com", "password"));
    }
}
