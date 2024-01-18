package com.univbuc.bookreview.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.univbuc.bookreview.controllers.UserBookController;
import com.univbuc.bookreview.dto.UserBookDto;
import com.univbuc.bookreview.models.UserBook;
import com.univbuc.bookreview.services.UserBookService;
import com.univbuc.bookreview.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserBookControllerTest {

    @Mock
    private UserBookService userBookService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserBookController userBookController;

    private final String token = "dummyToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMarkBookAsReadUnauthorized() {
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        ResponseEntity<?> response = userBookController.markBookAsRead(new UserBookDto(), token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testMarkBookAsReadSuccess() {
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);

        UserBookDto userBookDto = new UserBookDto();
        userBookDto.setBookId(1L);

        when(userBookService.markBookAsRead(eq(1L), eq(1L))).thenReturn(new UserBook());

        ResponseEntity<?> response = userBookController.markBookAsRead(userBookDto, token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetReadBooksUnauthorized() {
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        ResponseEntity<?> response = userBookController.getReadBooks(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetReadBooksSuccess() {
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(userBookService.getReadBooksByUser(anyLong())).thenReturn(Arrays.asList(new UserBook(), new UserBook()));

        ResponseEntity<?> response = userBookController.getReadBooks(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserBook> books = (List<UserBook>) response.getBody();
        assertEquals(2, books.size());
    }

    @Test
    void testDeleteUserBookUnauthorized() {
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        ResponseEntity<?> response = userBookController.deleteUserBook(1L, token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteUserBookSuccess() {
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(userBookService.deleteUserBook(anyLong(), anyLong())).thenReturn(true);

        ResponseEntity<?> response = userBookController.deleteUserBook(1L, token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteUserBookNotFound() {
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(userBookService.deleteUserBook(anyLong(), anyLong())).thenReturn(false);

        ResponseEntity<?> response = userBookController.deleteUserBook(1L, token);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
