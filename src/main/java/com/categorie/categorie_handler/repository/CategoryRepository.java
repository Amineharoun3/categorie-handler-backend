package com.categorie.categorie_handler.repository;



import com.categorie.categorie_handler.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE "
            + "(:isRoot IS NULL OR c.isRoot = :isRoot) AND "
            + "(:afterDate IS NULL OR c.creationDate >= :afterDate) AND "
            + "(:beforeDate IS NULL OR c.creationDate <= :beforeDate)")
    List<Category> findCategoriesWithFilters(@Param("isRoot") Boolean isRoot,
                                             @Param("afterDate") LocalDate afterDate,
                                             @Param("beforeDate") LocalDate beforeDate);

}
