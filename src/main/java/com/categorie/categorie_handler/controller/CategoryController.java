package com.categorie.categorie_handler.controller;

import com.categorie.categorie_handler.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import com.categorie.categorie_handler.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.stream.Collectors;
import java.util.List;

@RequestMapping("api/categories")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Categories", description = "Endpoints pour gérer les catégories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Créer une catégorie", description = "Créer une nouvelle catégorie avec les informations fournies")
    public Category createCategory(@RequestBody Category category) {
        System.out.println("Category received: " + category);
        return categoryService.createCategory(category);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les catégories", description = "Lister toutes les catégories disponibles")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une catégorie par ID", description = "Récupérer une catégorie spécifique en utilisant son ID")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/with-children")
    @Operation(summary = "Récupérer les catégories avec leurs enfants", description = "Lister toutes les catégories principales et leurs sous-catégories")
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
    @Operation(summary = "Paginer les catégories", description = "Récupérer une liste paginée de catégories")
    public Page<Category> getCategories(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(PageRequest.of(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une catégorie", description = "Mettre à jour une catégorie existante en utilisant son ID")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une catégorie", description = "Supprimer une catégorie en utilisant son ID")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des catégories", description = "Rechercher des catégories par nom, type ou date")
    public Page<Category> searchCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isRoot,
            @RequestParam(required = false) String afterDate,
            @RequestParam(required = false) String beforeDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryService.searchCategories(name, isRoot, afterDate, beforeDate, PageRequest.of(page, size));
    }
}
