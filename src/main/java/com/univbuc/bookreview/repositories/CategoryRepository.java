package com.univbuc.bookreview.repositories;

import com.univbuc.bookreview.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
