package com.univbuc.bookreview.controller;

import com.univbuc.bookreview.controllers.BookViewController;
import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.services.BookService;
import com.univbuc.bookreview.services.CategoryService;
import com.univbuc.bookreview.services.ReviewService;
import com.univbuc.bookreview.utilities.SecurityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BookViewController.class)
public class BookViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private SecurityUtils securityUtils;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testViewBooks() throws Exception {
        when(bookService.findPaginated(any(PageRequest.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/books/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("bookPage", "books"));

        verify(bookService, times(1)).findPaginated(any(PageRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddBookForm() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-book"))
                .andExpect(model().attributeExists("bookDto", "categories"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddBook() throws Exception {
        //create new category
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Sample Category");
        categoryService.addCategory(category);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Sample Book");
        bookDto.setAuthor("Sample Author");
        bookDto.setReleaseDate(new java.sql.Date(System.currentTimeMillis()));
        bookDto.setCategoryId(category.getId());
        bookDto.setDescription("Sample Description");


        when(bookService.addBook(any(BookDto.class))).thenReturn(new Book());

        mockMvc.perform(post("/books/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("bookDto", bookDto)
                        .with(csrf()))  // Include CSRF token here
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/view"));

        verify(bookService, times(1)).addBook(any(BookDto.class));
    }

    private ResultMatcher redirectedUrl(String path) {
        return result -> {
            String location = result.getResponse().getRedirectedUrl();
            if (!location.equals(path)) {
                throw new AssertionError("Expected redirect to " + path + ", but was to " + location);
            }
        };
    }
}
