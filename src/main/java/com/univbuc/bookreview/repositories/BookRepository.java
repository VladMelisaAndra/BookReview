package com.univbuc.bookreview.repositories;

import com.univbuc.bookreview.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByCategoryId(Long categoryId);

    List<Book> findByTitleContainingIgnoreCase(String title);
}
