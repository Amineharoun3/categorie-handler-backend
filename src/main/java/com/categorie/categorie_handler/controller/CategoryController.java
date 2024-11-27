package com.categorie.categorie_handler.controller;

import com.categorie.categorie_handler.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import com.categorie.categorie_handler.service.CategoryService;

import java.time.LocalDate;
import java.util.stream.Collectors;

import java.util.List;

@RequestMapping("api/categories")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        System.out.println("Category received: " + category);
        return categoryService.createCategory(category);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/with-children")
    public List<Category> getAllCategoriesWithChildren() {
        List<Category> allCategories = categoryService.getAllCategories();
        List<Category> rootCategories = allCategories.stream()
                .filter(category -> category.getParentCategory() == null)
                .collect(Collectors.toList());

        for (Category rootCategory : rootCategories) {
            setChildCategories(rootCategory, allCategories);
        }

        return rootCategories;
    }

    private void setChildCategories(Category parent, List<Category> allCategories) {
        List<Category> children = allCategories.stream()
                .filter(category -> category.getParentCategory() != null && category.getParentCategory().getId().equals(parent.getId()))
                .collect(Collectors.toList());

        parent.setChildren(children);

        for (Category child : children) {
            setChildCategories(child, allCategories);
        }
    }

    @GetMapping("/categories")
    public Page<Category> getCategories(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(PageRequest.of(page, size));
    }
    @GetMapping("/categories/filter")
    public List<Category> filterCategories(@RequestParam(required = false) Boolean isRoot,
                                           @RequestParam(required = false) LocalDate afterDate,
                                           @RequestParam(required = false) LocalDate beforeDate) {
        return categoryService.filterCategories(isRoot, afterDate, beforeDate);
    }



    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }


    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
