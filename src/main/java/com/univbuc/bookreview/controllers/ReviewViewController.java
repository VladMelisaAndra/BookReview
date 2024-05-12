package com.univbuc.bookreview.controllers;

import com.univbuc.bookreview.dto.ReviewDto;
import com.univbuc.bookreview.models.Review;
import com.univbuc.bookreview.services.ReviewService;
import com.univbuc.bookreview.utilities.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewViewController {

    @Autowired
    private ReviewService reviewService;

    private final SecurityUtils securityUtils;

    @Autowired
    public ReviewViewController(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    // Adding a review
    @PostMapping(path = "/add")
    public String addReview(@ModelAttribute ReviewDto reviewDto, RedirectAttributes redirectAttributes) {
        reviewDto.setUserId(securityUtils.getCurrentUserId());
        reviewService.addReview(reviewDto);
        redirectAttributes.addFlashAttribute("message", "Review added successfully!");
        return "redirect:/books/" + reviewDto.getBookId();  // Redirect back to the book details page
    }


    // Update review form
    @GetMapping("/edit/{id}")
    public String editReviewForm(@PathVariable Long id, Model model) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return "redirect:/";
        }
        model.addAttribute("review", review);
        return "edit-review";
    }

    // Updating a review
    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute ReviewDto reviewDto, RedirectAttributes redirectAttributes) {
        reviewService.updateReview(id, reviewDto);
        redirectAttributes.addFlashAttribute("message", "Review updated successfully!");
        return "redirect:/books/" + reviewDto.getBookId();
    }

    // Deleting a review
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            redirectAttributes.addFlashAttribute("error", "Review not found!");
            return "redirect:/";
        }
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("message", "Review deleted successfully!");
        return "redirect:/books/" + review.getBook().getId();
    }
}
