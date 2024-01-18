package com.univbuc.bookreview.services;

import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Category category;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setCategoryName("Fiction");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setCategory(category);

        bookDto = new BookDto();
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Author");
        bookDto.setCategoryId(1L);
    }

    @Test
    void addBook() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.addBook(bookDto);

        assertNotNull(savedBook);
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals("Author", savedBook.getAuthor());
    }

    @Test
    void updateBook() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("Updated Title");
        updatedBookDto.setAuthor("Updated Author");
        updatedBookDto.setCategoryId(1L);

        Book updatedBook = bookService.updateBook(1L, updatedBookDto);

        assertNotNull(updatedBook);
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Updated Author", updatedBook.getAuthor());
    }

    @Test
    void getBookByIdFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(1L);

        assertNotNull(foundBook);
        assertEquals("Test Book", foundBook.getTitle());
    }

    @Test
    void getBookByIdNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        Book foundBook = bookService.getBookById(1L);

        assertNull(foundBook);
    }

    @Test
    void searchBooksByTitle() {
        when(bookRepository.findByTitleContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(book));

        List<Book> books = bookService.searchBooksByTitle("Test");

        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
    }

    @Test
    void getAllBooks() {
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        List<Book> books = bookService.getAllBooks();

        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
    }

    @Test
    void deleteBookExists() {
        when(bookRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(bookRepository).deleteById(anyLong());

        assertTrue(bookService.deleteBook(1L));
    }

    @Test
    void deleteBookNotExists() {
        when(bookRepository.existsById(anyLong())).thenReturn(false);

        assertFalse(bookService.deleteBook(1L));
    }
}
