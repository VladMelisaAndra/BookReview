package com.univbuc.bookreview.services;

import com.univbuc.bookreview.dto.ReviewDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.models.Review;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.ReviewRepository;
import com.univbuc.bookreview.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    public Review addReview(ReviewDto reviewDto) {
        if (reviewDto.getBookId() == null || reviewDto.getUserId() == null) {
            throw new IllegalArgumentException("Book ID and User ID must not be null"+reviewDto.getUserId());
        }
        Review review = new Review();
        Logger logger = Logger.getLogger("review");
        logger.info("ReviewDto " + reviewDto);
        Book book = bookRepository.findById(reviewDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        review.setBook(book);
        review.setUser(user);
        review.setStars(reviewDto.getStars());
        review.setComment(reviewDto.getComment());
        review.setDate(new Date());

        return reviewRepository.save(review);
    }

    public Review updateReview(Long reviewId, ReviewDto reviewDto) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        existingReview.setStars(reviewDto.getStars());
        existingReview.setComment(reviewDto.getComment());

        return reviewRepository.save(existingReview);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElse(null);
    }

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

}
