package com.univbuc.bookreview.services;

import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.models.UserBook;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.UserBookRepository;
import com.univbuc.bookreview.repositories.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserBookServiceTest {

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UserBookService userBookService;

    private User user;
    private Book book;
    private UserBook userBook;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        book = new Book();
        book.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Test");

        book.setCategory(category);

        userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
    }

    @Test
    void markBookAsRead() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(userBookRepository.save(any(UserBook.class))).thenReturn(userBook);

        UserBook markedUserBook = userBookService.markBookAsRead(1L, 1L);

        assertNotNull(markedUserBook);
        assertEquals(user, markedUserBook.getUser());
        assertEquals(book, markedUserBook.getBook());
    }

    @Test
    void markBookAsRead_UserOrBookNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userBookService.markBookAsRead(1L, 1L));
    }

    @Test
    void getReadBooksByUser() {
        when(userBookRepository.findByUserId(anyLong())).thenReturn(Collections.singletonList(userBook));

        List<BookDto> readBooks = userBookService.getReadBooksByUser(1L);

        assertFalse(readBooks.isEmpty());
        assertEquals(1, readBooks.size());

        assertEquals(userBook.getBook().getTitle(), readBooks.get(0).getTitle());
    }

    @Test
    void deleteUserBook_Exists() {
        when(userBookRepository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(userBook));
        doNothing().when(userBookRepository).delete(any(UserBook.class));

        boolean deleted = userBookService.deleteUserBook(1L, 1L);

        assertTrue(deleted);
    }

    @Test
    void deleteUserBook_NotExists() {
        when(userBookRepository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.empty());

        boolean deleted = userBookService.deleteUserBook(1L, 1L);

        assertFalse(deleted);
    }
}