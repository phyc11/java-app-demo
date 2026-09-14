package com.example.task.controller;

import com.example.common.dto.ApiResponse;
import com.example.task.model.Category;
import com.example.task.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.ok("Categories retrieved successfully", categories);
    }

    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ApiResponse.ok("Category created successfully", createdCategory);
    }
}
