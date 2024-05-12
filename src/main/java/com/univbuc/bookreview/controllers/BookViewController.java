package com.univbuc.bookreview.controllers;

import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.dto.ReviewDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.models.Review;
import com.univbuc.bookreview.services.BookService;
import com.univbuc.bookreview.services.CategoryService;
import com.univbuc.bookreview.services.ReviewService;
import com.univbuc.bookreview.utilities.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookViewController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReviewService reviewService;

    private final SecurityUtils securityUtils;

    private static final Logger log = LoggerFactory.getLogger(BookViewController.class);

    @Autowired
    public BookViewController(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // View all books
    @GetMapping("/view")
    public String viewBooks(Model model, @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size,
                            @RequestParam("sortDir") Optional<String> sortDir) {
        List<Book> books = bookService.getAllBooks();
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortDirection = sortDir.orElse("desc");

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "releaseDate");
        Page<Book> bookPage = bookService.findPaginated(PageRequest.of(currentPage - 1, pageSize, sort));

        model.addAttribute("bookPage", bookPage);
        model.addAttribute("books", books);
        return "books";
    }

    // Display form to add a new book
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-book";
    }

    // Process the submission of add book form
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("bookDto") BookDto bookDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Please correct the following errors: ");
            bindingResult.getAllErrors().forEach(error -> {
                errorMsg.append(error.getDefaultMessage()).append(" ");
            });

            redirectAttributes.addFlashAttribute("error", errorMsg.toString());
            redirectAttributes.addFlashAttribute("bookDto", bookDto);  // Optionally retain input data
            return "redirect:/books/add";
        }

        bookService.addBook(bookDto);
        redirectAttributes.addFlashAttribute("message", "Book added successfully!");
        return "redirect:/books/view";
    }


    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String title, Model model) {
        List<Book> books;
        if (title != null && !title.isEmpty()) {
            books = bookService.searchBooksByTitle(title);
        } else {
            books = Collections.emptyList();
            log.info("No search criteria provided");
        }
        model.addAttribute("books", books);
        return "search-books";
    }


    // Display form to edit a book
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "The requested book was not found.");
            return "redirect:/books/view";  // Redirect if book not found
        }
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setDescription(book.getDescription());
        bookDto.setReleaseDate(book.getReleaseDate());
        bookDto.setCategoryId(book.getCategory().getId());

        model.addAttribute("book", bookDto);
        model.addAttribute("categories", bookService.getAllCategories());
        return "edit-book";
    }


    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id, @Valid @ModelAttribute("bookDto") BookDto bookDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append(" "));
            redirectAttributes.addFlashAttribute("error", errorMsg.toString());
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/books/edit/" + id;
        }

        if (id == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid book ID.");
            return "redirect:/books/view";
        }

        Book updatedBook = bookService.updateBook(id, bookDto);
        if (updatedBook == null) {
            redirectAttributes.addFlashAttribute("error", "Error updating book. It may no longer exist.");
            return "redirect:/books/edit/" + id;
        }

        redirectAttributes.addFlashAttribute("message", "Book updated successfully!");
        return "redirect:/books/view";
    }


    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean isDeleted = bookService.deleteBook(id);
        if (!isDeleted) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully!");
        }
        return "redirect:/books/view";  // Redirect after deletion
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return "redirect:/books/view";  // Redirect if book not found
        }
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setDescription(book.getDescription());
        bookDto.setReleaseDate(book.getReleaseDate());
        if (book.getCategory() != null) {
            bookDto.setCategoryId(book.getCategory().getId());
        }

        Map<Long, String> categoryMap = bookService.getAllCategories().stream()
                .collect(Collectors.toMap(Category::getId, Category::getCategoryName));
        model.addAttribute("book", bookDto);
        model.addAttribute("categoryMap", categoryMap);
        List<Review> reviews = reviewService.getReviewsByBookId(id);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewDto", new ReviewDto()); // For adding a new review
        return "book-detail";
    }


}