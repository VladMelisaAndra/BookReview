package com.univbuc.bookreview.services;

import com.univbuc.bookreview.domain.security.User;
import com.univbuc.bookreview.dto.ReviewDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Review;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.ReviewRepository;
import com.univbuc.bookreview.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Book book;
    private User user;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);

        user = new User();
        user.setId(1L);

        review = new Review();
        review.setId(1L);
        review.setBook(book);
        review.setUser(user);
        review.setStars(5);
        review.setComment("Great book!");
        review.setDate(new Date());

        reviewDto = new ReviewDto();
        reviewDto.setBookId(1L);
        reviewDto.setUserId(1L);
        reviewDto.setStars(5);
        reviewDto.setComment("Great book!");
    }

    @Test
    void addReview() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review savedReview = reviewService.addReview(reviewDto);

        assertNotNull(savedReview);
        assertEquals(5, savedReview.getStars());
        assertEquals("Great book!", savedReview.getComment());
    }

    @Test
    void updateReview() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDto updatedReviewDto = new ReviewDto();
        updatedReviewDto.setStars(4);
        updatedReviewDto.setComment("Good book!");

        Review updatedReview = reviewService.updateReview(1L, updatedReviewDto);

        assertNotNull(updatedReview);
        assertEquals(4, updatedReview.getStars());
        assertEquals("Good book!", updatedReview.getComment());
    }

    @Test
    void getAllReviews() {
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));

        List<Review> reviews = reviewService.getAllReviews();

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
    }

    @Test
    void getReviewByIdFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        Review foundReview = reviewService.getReviewById(1L);

        assertNotNull(foundReview);
        assertEquals("Great book!", foundReview.getComment());
    }

    @Test
    void getReviewByIdNotFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        Review foundReview = reviewService.getReviewById(1L);

        assertNull(foundReview);
    }

    @Test
    void getReviewsByBookId() {
        when(reviewRepository.findByBookId(anyLong())).thenReturn(Collections.singletonList(review));

        List<Review> reviews = reviewService.getReviewsByBookId(1L);

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
    }

    @Test
    void deleteReviewExists() {
        when(reviewRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> reviewService.deleteReview(1L));
    }

    @Test
    void deleteReviewNotExists() {
        when(reviewRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(1L));
    }
}
