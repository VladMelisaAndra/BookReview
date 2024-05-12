package com.univbuc.bookreview.controllers;
import com.univbuc.bookreview.dto.BookDto;
import com.univbuc.bookreview.models.Book;
import com.univbuc.bookreview.models.Category;
import com.univbuc.bookreview.services.BookService;
import com.univbuc.bookreview.services.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    @Autowired
    private CategoryService categoryService;

    // Display all categories with forms for add, edit, and delete
    @GetMapping
    public String viewCategories(Model model) {
        model.addAttribute("newCategory", new Category());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    // Add a new category
    @PostMapping("/add")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.addCategory(category);
        redirectAttributes.addFlashAttribute("message", "Category added successfully!");
        return "redirect:/categories";
    }

    // Update an existing category
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        Category category = new Category();
        category.setId(id);
        category.setCategoryName(name);
        categoryService.updateCategory(id, category);
        redirectAttributes.addFlashAttribute("message", "Category updated successfully!");
        return "redirect:/categories";
    }

    // Delete a category
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (categoryService.canDeleteCategory(id)) {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Category deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Category cannot be deleted as it is assigned to one or more books.");
        }
        return "redirect:/categories";
    }
}
