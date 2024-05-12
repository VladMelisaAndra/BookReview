package com.univbuc.bookreview.controller;

import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.services.BookService;
import com.univbuc.bookreview.services.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testViewBooks() throws Exception {
        mockMvc.perform(get("/books/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("bookPage", "books"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddBookForm() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-book"))
                .andExpect(model().attributeExists("bookDto", "categories"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddBook() throws Exception {
        Category category = new Category();
        category.setCategoryName("Sample Category");
        category = categoryService.addCategory(category);

        mockMvc.perform(post("/books/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Sample Book")
                        .param("author", "Author Test")
                        .param("releaseDate", "2021-01-01")
                        .param("categoryId", category.getId().toString())
                        .param("description", "Test Description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/view"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testEditBookForm() throws Exception {
        Category category = new Category();
        category.setCategoryName("Sample Category");
        category = categoryService.addCategory(category);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Sample Book");
        bookDto.setAuthor("Author Test");
        bookDto.setReleaseDate(new Date());
        bookDto.setDescription("Test Description");
        bookDto.setCategoryId(category.getId());
        Book savedBook = bookService.addBook(bookDto);

        mockMvc.perform(get("/books/edit/{id}", savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-book"))
                .andExpect(model().attributeExists("book", "categories"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateBook() throws Exception {
        Category category = new Category();
        category.setCategoryName("Sample Category");
        category = categoryService.addCategory(category);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("Sample Book");
        bookDto.setAuthor("Author Test");
        bookDto.setReleaseDate(new Date());
        bookDto.setDescription("Test Description");
        bookDto.setCategoryId(category.getId());
        Book savedBook = bookService.addBook(bookDto);

        mockMvc.perform(post("/books/update/{id}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Updated Title")
                        .param("author", "Updated Author")
                        .param("description", "Updated Description")
                        .param("releaseDate", "2022-01-01")
                        .param("categoryId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/view"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteBook() throws Exception {
        mockMvc.perform(get("/books/delete/{id}", 1).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/view"));
    }
}
