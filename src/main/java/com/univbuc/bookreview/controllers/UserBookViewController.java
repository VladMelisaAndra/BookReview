package com.univbuc.bookreview.controllers;

import com.univbuc.bookreview.domain.security.User;
import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.repositories.security.UserRepository;
import com.univbuc.bookreview.services.UserBookService;
import com.univbuc.bookreview.utilities.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user-books")
public class UserBookViewController {

    @Autowired
    private UserBookService userBookService;

    @Autowired
    private UserRepository userRepository;

    private final SecurityUtils securityUtils;

    @Autowired
    public UserBookViewController(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public String listUserBooks(Model model) {
        Long userId = securityUtils.getCurrentUserId();
        model.addAttribute("books", userBookService.getReadBooksByUser(userId));
        return "user-books";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam("bookId") Long bookId, RedirectAttributes redirectAttributes) {
        Long userId = securityUtils.getCurrentUserId();
        //check if book is already in the list
        if (userBookService.isBookInList(userId, bookId)) {
            redirectAttributes.addFlashAttribute("error", "Book already in your list!");
            return "redirect:/user-books";
        }
        userBookService.markBookAsRead(userId, bookId);
        redirectAttributes.addFlashAttribute("message", "Book added successfully to your read list!");
        return "redirect:/user-books";
    }


    // Process the deletion of a book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long userId = securityUtils.getCurrentUserId();
        if (userBookService.deleteUserBook(userId, id)) {
            redirectAttributes.addFlashAttribute("message", "Book removed successfully from your read list!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found in your list!");
        }
        return "redirect:/user-books";
    }


}
