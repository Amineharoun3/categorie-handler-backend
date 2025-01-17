package com.categorie.categorie_handler.service;
import com.categorie.categorie_handler.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.categorie.categorie_handler.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate; // Critères pour les requêtes JPA
import java.util.ArrayList; // Pour les listes dynamiques
import java.util.List; // Pour les listes
import org.springframework.data.jpa.domain.Specification; // Pour les spécifications JPA

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

    private Category findRootCategory(Category category) {
    while (category.getParentCategory() != null) {
        category = category.getParentCategory();
    }
    return category;
}


public Page<Category> searchCategories(String name, Boolean isRoot, String afterDate, String beforeDate, Pageable pageable) {
    LocalDate startDate = (afterDate != null) ? LocalDate.parse(afterDate) : null;
    LocalDate endDate = (beforeDate != null) ? LocalDate.parse(beforeDate) : null;

    return categoryRepository.findAll((root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (isRoot != null) {
            if (isRoot) {
                predicates.add(criteriaBuilder.isNull(root.get("parentCategory")));
            } else {
                predicates.add(criteriaBuilder.isNotNull(root.get("parentCategory")));
            }
        }

        if (startDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), endDate));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }, pageable);
}


}