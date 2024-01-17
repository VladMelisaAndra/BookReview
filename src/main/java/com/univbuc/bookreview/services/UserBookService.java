package com.univbuc.bookreview.services;

import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.User;
import com.univbuc.bookreview.models.UserBook;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.UserBookRepository;
import com.univbuc.bookreview.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserBookService {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public UserBook markBookAsRead(Long userId, Long bookId) {
        Optional<User> user = userRepository.findById(userId);
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
    public List<UserBook> getReadBooksByUser(Long userId) {
        return userBookRepository.findByUserId(userId);
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

}
