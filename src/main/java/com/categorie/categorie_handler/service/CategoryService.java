package com.categorie.categorie_handler.service;
import com.categorie.categorie_handler.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.categorie.categorie_handler.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        // Vérifier si un parentCategory est défini
        if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
            // Charger le parent à partir de la base de données
            Category parent = categoryRepository.findById(category.getParentCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent); // Associer le parent à la catégorie
        }

        // Vérifier et initialiser d'autres champs si nécessaire
        if (category.getCreatedDate() == null) {
            category.setCreatedDate(LocalDate.now());
        }

        // Sauvegarder la catégorie dans la base de données
        return categoryRepository.save(category);
    }


    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Category> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public List<Category> filterCategories(Boolean isRoot, LocalDate afterDate, LocalDate beforeDate) {
        return categoryRepository.findCategoriesWithFilters(isRoot, afterDate, beforeDate);
    }


    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        System.out.println("Reçu pour mise à jour : " + categoryDetails);
        System.out.println("Parent Category : " + (categoryDetails.getParentCategory() == null ? "null" : categoryDetails.getParentCategory().getId()));

        // Récupération de la catégorie existante
        Category category = getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        // Mise à jour du nom de la catégorie
        if (categoryDetails.getName() != null) {
            category.setName(categoryDetails.getName());
        }

        // Mise à jour du parent
        if (categoryDetails.getParentCategory() != null) {
            Long parentId = categoryDetails.getParentCategory().getId();
            if (parentId != null) {
                Category parent = categoryRepository.findById(parentId)
                        .orElseThrow(() -> new RuntimeException("Parent category not found"));

                // Vérifie si un parent est valide avant de l'associer
                category.setParentCategory(parent);
            }
        } else {
            // Dissocie le parent si aucune catégorie parent n'est sélectionnée
            category.setParentCategory(null);
        }

        // Mise à jour des enfants (si fourni)
        if (categoryDetails.getChildren() != null) {
            category.getChildren().clear(); // Supprime les enfants existants
            for (Category child : categoryDetails.getChildren()) {
                Category existingChild = categoryRepository.findById(child.getId())
                        .orElseThrow(() -> new RuntimeException("Child category not found"));
                existingChild.setParentCategory(category); // Associe l'enfant à la catégorie
                category.getChildren().add(existingChild);
            }
        }

        // Sauvegarde la catégorie mise à jour
        return categoryRepository.save(category);
    }





    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }


    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}