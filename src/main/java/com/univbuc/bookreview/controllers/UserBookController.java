package com.univbuc.bookreview.controllers;

import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.dto.UserBookDto;
import com.univbuc.bookreview.models.UserBook;
import com.univbuc.bookreview.services.UserBookService;
import com.univbuc.bookreview.utilities.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-books")
@SecurityRequirement(name = "Bearer")
public class UserBookController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserBookService userBookService;

    // Mark a book as read for the logged-in user
    @PostMapping("/add")
    public ResponseEntity<?> markBookAsRead(@RequestBody UserBookDto userBookDto, @RequestHeader("Authorization") String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid or missing token");
        }

        Long userId = jwtUtil.extractUserId(token);
        UserBook userBook = userBookService.markBookAsRead(userId, userBookDto.getBookId());
        return ResponseEntity.ok("Book added in read list");
    }

    // Get read books for the logged-in user
    @GetMapping
    public ResponseEntity<?> getReadBooks(@RequestHeader("Authorization") String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid or missing token");
        }

        Long userId = jwtUtil.extractUserId(token);
        List<BookDto> readBooks = userBookService.getReadBooksByUser(userId);
        return ResponseEntity.ok(readBooks);
    }

    // Delete a specific book from the user's read list
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteUserBook(@PathVariable Long bookId, @RequestHeader("Authorization") String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid or missing token");
        }

        Long userId = jwtUtil.extractUserId(token);
        boolean isDeleted = userBookService.deleteUserBook(userId, bookId);
        if (isDeleted) {
            return ResponseEntity.ok("Book removed from read list");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found in read list");
        }
    }

}
