package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByCategoryAndEnabled(Category category, Boolean enabled, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByEnabled(Boolean enabled, Pageable pageable);

    @Query(value = """
        SELECT * FROM product p 
        WHERE p.category_id IN (
            SELECT c.id FROM category c
            WHERE c.id = :id OR c.parent_id = :id
        )
    """, nativeQuery = true)
    Page<Product> findByCategoryAndSubcategories(@Param("id") Long id, Pageable pageable);
}
