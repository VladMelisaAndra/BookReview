package com.univbuc.bookreview.services;

import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setCategoryName(categoryDetails.getCategoryName());
        return categoryRepository.save(category);
    }

    public boolean canDeleteCategory(Long id) {
        return !bookRepository.existsByCategoryId(id);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
