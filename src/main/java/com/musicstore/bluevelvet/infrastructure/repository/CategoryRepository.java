package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories @Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByIsRootIsTrue();
    Page<Category> findByIsRootIsTrue(Pageable pageable);
    List<Category> findByParent(Category category, Sort sort);

    /**
     * Busca categorias por nome contendo (LIKE)
     */
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
