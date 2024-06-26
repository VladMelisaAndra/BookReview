package com.univbuc.bookreview.services;

import com.univbuc.bookreview.domain.security.User;
import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.UserBook;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.UserBookRepository;
import com.univbuc.bookreview.repositories.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserBookService {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public UserBook markBookAsRead(Long userId, Long bookId) {
        Optional<User> user = userRepository.findById(Math.toIntExact(userId));
        Optional<Book> book = bookRepository.findById(bookId);

        if(user.isPresent() && book.isPresent()) {
            UserBook userBook = new UserBook();
            userBook.setUser(user.get());
            userBook.setBook(book.get());
            return userBookRepository.save(userBook);
        } else {
            throw new RuntimeException("User or Book not found");
        }
    }

    // Get read books by a specific user
    public List<BookDto> getReadBooksByUser(Long userId) {
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);
        return userBooks.stream()
                .map(userBook -> mapToBookResponseDto(userBook.getBook()))
                .collect(Collectors.toList());
    }

    private BookDto mapToBookResponseDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setReleaseDate(book.getReleaseDate());
        dto.setCategoryId(book.getCategory().getId());
        dto.setDescription(book.getDescription());
        return dto;
    }

    // Delete a specific book from a user's read list
    public boolean deleteUserBook(Long userId, Long bookId) {
        Optional<UserBook> userBook = userBookRepository.findByUserIdAndBookId(userId, bookId);
        if (userBook.isPresent()) {
            userBookRepository.delete(userBook.get());
            return true;
        }
        return false;
    }

    public boolean isBookInList(Long userId, Long bookId) {
        return userBookRepository.findByUserIdAndBookId(userId, bookId).isPresent();
    }
}
