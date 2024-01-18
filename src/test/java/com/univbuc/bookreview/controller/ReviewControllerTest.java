package com.univbuc.bookreview.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.univbuc.bookreview.controllers.ReviewController;
import com.univbuc.bookreview.dto.ReviewDto;
import com.univbuc.bookreview.models.Review;
import com.univbuc.bookreview.services.ReviewService;
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
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ReviewController reviewController;

    private final String token = "dummyToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReviewUnauthorized() {
        when(jwtUtil.isTokenValid(token)).thenReturn(false);

        ResponseEntity<?> response = reviewController.addReview(new ReviewDto(), token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testAddReviewSuccess() {
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(reviewService.addReview(any(ReviewDto.class))).thenReturn(new Review());

        ResponseEntity<?> response = reviewController.addReview(new ReviewDto(), token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testGetReviewsByBookId() {
        when(reviewService.getReviewsByBookId(1L)).thenReturn(Arrays.asList(new Review(), new Review()));

        ResponseEntity<List<Review>> response = reviewController.getReviewsByBookId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testUpdateReview() {
        Review updatedReview = new Review();
        when(reviewService.updateReview(eq(1L), any(ReviewDto.class))).thenReturn(updatedReview);

        ResponseEntity<?> response = reviewController.updateReview(1L, new ReviewDto());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReview, response.getBody());
    }

    @Test
    void testDeleteReview() {
        doNothing().when(reviewService).deleteReview(1L);

        ResponseEntity<HttpStatus> response = reviewController.deleteReview(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetReviewById() {
        when(reviewService.getReviewsByBookId(1L)).thenReturn(Arrays.asList(new Review()));

        ResponseEntity<?> response = reviewController.getReviewsById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
