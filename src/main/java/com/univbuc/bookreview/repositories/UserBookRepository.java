package com.univbuc.bookreview.repositories;

import com.univbuc.bookreview.models.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    // Method to find books read by a specific user
    List<UserBook> findByUserId(Long userId);

    // Method to find a specific book in a user's read list
    Optional<UserBook> findByUserIdAndBookId(Long userId, Long bookId);
}