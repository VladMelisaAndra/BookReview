package com.univbuc.bookreview.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.univbuc.bookreview.controllers.CategoryController;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.services.CategoryService;
import com.univbuc.bookreview.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CategoryController categoryController;

    private final String token = "dummyToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCategoryUnauthorized() {
        when(jwtUtil.isAdmin(token)).thenReturn(false);

        ResponseEntity<?> response = categoryController.addCategory(new Category(), token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testAddCategorySuccess() {
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        when(categoryService.addCategory(any(Category.class))).thenReturn(new Category());

        ResponseEntity<?> response = categoryController.addCategory(new Category(), token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testGetAllCategories() {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(new Category(), new Category()));

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetCategoryByIdSuccess() {
        Category category = new Category();
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
    }

    @Test
    void testUpdateCategoryUnauthorized() {
        when(jwtUtil.isAdmin(token)).thenReturn(false);

        ResponseEntity<?> response = categoryController.updateCategory(1L, new Category(), token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateCategorySuccess() {
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        Category updatedCategory = new Category();
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        ResponseEntity<?> response = categoryController.updateCategory(1L, new Category(), token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void testDeleteCategoryUnauthorized() {
        when(jwtUtil.isAdmin(token)).thenReturn(false);

        ResponseEntity<?> response = categoryController.deleteCategory(1L, token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteCategoryAssignedToBooks() {
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        when(categoryService.canDeleteCategory(1L)).thenReturn(false);

        ResponseEntity<?> response = categoryController.deleteCategory(1L, token);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category cannot be deleted as it is assigned to one or more books.", response.getBody());
    }

    @Test
    void testDeleteCategorySuccess() {
        when(jwtUtil.isAdmin(token)).thenReturn(true);
        when(categoryService.canDeleteCategory(1L)).thenReturn(true);

        ResponseEntity<?> response = categoryController.deleteCategory(1L, token);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
