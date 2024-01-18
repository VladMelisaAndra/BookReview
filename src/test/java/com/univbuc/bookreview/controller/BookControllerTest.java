package com.univbuc.bookreview.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.univbuc.bookreview.controllers.BookController;
import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.services.BookService;
import com.univbuc.bookreview.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddBookUnauthorized() {
        String token = "invalidToken";
        when(jwtUtil.isAdmin(token)).thenReturn(false);

        ResponseEntity<?> response = bookController.addBook(new BookDto(), token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testAddBookSuccess() {
        String token = "validToken";
        when(jwtUtil.isAdmin(token)).thenReturn(true);

        BookDto bookDto = new BookDto();
        Book book = new Book();
        when(bookService.addBook(bookDto)).thenReturn(book);

        ResponseEntity<?> response = bookController.addBook(bookDto, token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testSearchBooksByTitleNotFound() {
        when(bookService.searchBooksByTitle("Unknown")).thenReturn(new ArrayList<>());

        ResponseEntity<List<Book>> response = bookController.searchBooksByTitle("Unknown");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSearchBooksByTitleSuccess() {
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        when(bookService.searchBooksByTitle("Known")).thenReturn(books);

        ResponseEntity<List<Book>> response = bookController.searchBooksByTitle("Known");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testUpdateBookUnauthorized() {
        String token = "invalidToken";
        when(jwtUtil.isAdmin(token)).thenReturn(false);

        ResponseEntity<?> response = bookController.updateBook(1L, new BookDto(), token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateBookNotFound() {
        String token = "validToken";
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        when(bookService.updateBook(eq(1L), any(BookDto.class))).thenReturn(null);

        ResponseEntity<?> response = bookController.updateBook(1L, new BookDto(), token);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateBookSuccess() {
        String token = "validToken";
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        Book updatedBook = new Book();
        when(bookService.updateBook(eq(1L), any(BookDto.class))).thenReturn(updatedBook);

        ResponseEntity<?> response = bookController.updateBook(1L, new BookDto(), token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedBook, response.getBody());
    }

    @Test
    void testGetBookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        ResponseEntity<Book> response = bookController.getBook(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetBookSuccess() {
        Book book = new Book();
        when(bookService.getBookById(1L)).thenReturn(book);

        ResponseEntity<Book> response = bookController.getBook(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book, response.getBody());
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = new ArrayList<>();
        when(bookService.getAllBooks()).thenReturn(books);

        ResponseEntity<List<Book>> response = bookController.getAllBooks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    void testDeleteBookUnauthorized() {
        String token = "invalidToken";
        when(jwtUtil.isAdmin(token)).thenReturn(false);

        ResponseEntity<?> response = bookController.deleteBook(1L, token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteBookNotFound() {
        String token = "validToken";
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        when(bookService.deleteBook(1L)).thenReturn(false);

        ResponseEntity<?> response = bookController.deleteBook(1L, token);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteBookSuccess() {
        String token = "validToken";
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        when(bookService.deleteBook(1L)).thenReturn(true);

        ResponseEntity<?> response = bookController.deleteBook(1L, token);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


}
