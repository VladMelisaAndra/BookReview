package com.univbuc.bookreview.services;

import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.repositories.BookRepository;
import com.univbuc.bookreview.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setCategoryName("Fiction");
    }

    @Test
    void addCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category savedCategory = categoryService.addCategory(category);

        assertNotNull(savedCategory);
        assertEquals("Fiction", savedCategory.getCategoryName());
    }

    @Test
    void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

        List<Category> categories = categoryService.getAllCategories();

        assertFalse(categories.isEmpty());
        assertEquals(1, categories.size());
    }

    @Test
    void getCategoryByIdFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        Optional<Category> foundCategory = categoryService.getCategoryById(1L);

        assertTrue(foundCategory.isPresent());
        assertEquals("Fiction", foundCategory.get().getCategoryName());
    }

    @Test
    void getCategoryByIdNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Category> foundCategory = categoryService.getCategoryById(1L);

        assertFalse(foundCategory.isPresent());
    }

    @Test
    void updateCategory() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Science Fiction");

        Category savedCategory = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(savedCategory);
        assertEquals("Science Fiction", savedCategory.getCategoryName());
    }

    @Test
    void canDeleteCategoryTrue() {
        when(bookRepository.existsByCategoryId(anyLong())).thenReturn(false);

        assertTrue(categoryService.canDeleteCategory(1L));
    }

    @Test
    void canDeleteCategoryFalse() {
        when(bookRepository.existsByCategoryId(anyLong())).thenReturn(true);

        assertFalse(categoryService.canDeleteCategory(1L));
    }

    @Test
    void deleteCategory() {
        doNothing().when(categoryRepository).deleteById(anyLong());

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
