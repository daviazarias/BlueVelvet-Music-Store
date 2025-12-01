package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para CategoryRepository
 * Valida as operações de banco de dados para categorias
 */
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category rootCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        rootCategory = Category.builder()
                .name("Rock")
                .description("Categoria de Rock")
                .enabled(true)
                .parentId(null)
                .build();

        childCategory = Category.builder()
                .name("Hard Rock")
                .description("Subcategoria de Hard Rock")
                .enabled(true)
                .parentId(null) // Será definido após salvar a categoria raiz
                .build();
    }

    /**
     * Teste: Salvar categoria raiz
     */
    @Test
    void testSaveRootCategory() {
        Category saved = categoryRepository.save(rootCategory);

        assertNotNull(saved.getId());
        assertEquals("Rock", saved.getName());
        assertTrue(saved.getEnabled());
        assertNull(saved.getParentId());
    }

    /**
     * Teste: Salvar categoria filha
     */
    @Test
    void testSaveChildCategory() {
        Category savedRoot = categoryRepository.save(rootCategory);
        childCategory.setParentId(savedRoot.getId());
        Category savedChild = categoryRepository.save(childCategory);

        assertNotNull(savedChild.getId());
        assertEquals("Hard Rock", savedChild.getName());
        assertEquals(savedRoot.getId(), savedChild.getParentId());
    }

    /**
     * Teste: Buscar categoria por ID
     */
    @Test
    void testFindCategoryById() {
        Category saved = categoryRepository.save(rootCategory);

        Optional<Category> found = categoryRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Rock", found.get().getName());
    }

    /**
     * Teste: Buscar categoria por ID (não encontrada)
     */
    @Test
    void testFindCategoryByIdNotFound() {
        Optional<Category> found = categoryRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    /**
     * Teste: Listar todas as categorias raiz
     */
    @Test
    void testFindAllRootCategories() {
        categoryRepository.save(rootCategory);

        Category jazz = Category.builder()
                .name("Jazz")
                .enabled(true)
                .parentId(null)
                .build();
        categoryRepository.save(jazz);

        Page<Category> roots = categoryRepository.findAll(PageRequest.of(0, 10));

        assertEquals(2, roots.getContent().size());
    }

    /**
     * Teste: Buscar categorias por nome (filtro)
     */
    @Test
    void testFindCategoryByNameContaining() {
        categoryRepository.save(rootCategory);

        Page<Category> results = categoryRepository.findByNameContainingIgnoreCase("rock", PageRequest.of(0, 10));

        assertEquals(1, results.getContent().size());
        assertEquals("Rock", results.getContent().get(0).getName());
    }

    /**
     * Teste: Buscar categorias por nome (case-insensitive)
     */
    @Test
    void testFindCategoryByNameCaseInsensitive() {
        categoryRepository.save(rootCategory);

        Page<Category> resultsLower = categoryRepository.findByNameContainingIgnoreCase("rock", PageRequest.of(0, 10));
        Page<Category> resultsUpper = categoryRepository.findByNameContainingIgnoreCase("ROCK", PageRequest.of(0, 10));

        assertEquals(1, resultsLower.getContent().size());
        assertEquals(1, resultsUpper.getContent().size());
    }

    /**
     * Teste: Atualizar categoria
     */
    @Test
    void testUpdateCategory() {
        Category saved = categoryRepository.save(rootCategory);
        saved.setName("Rock Atualizado");
        saved.setEnabled(false);

        Category updated = categoryRepository.save(saved);

        assertEquals("Rock Atualizado", updated.getName());
        assertFalse(updated.getEnabled());
    }

    /**
     * Teste: Deletar categoria
     */
    @Test
    void testDeleteCategory() {
        Category saved = categoryRepository.save(rootCategory);
        Long id = saved.getId();

        categoryRepository.deleteById(id);

        Optional<Category> found = categoryRepository.findById(id);
        assertFalse(found.isPresent());
    }

    /**
     * Teste: Paginação de categorias
     */
    @Test
    void testCategoryPagination() {
        for (int i = 1; i <= 15; i++) {
            categoryRepository.save(Category.builder()
                    .name("Category " + i)
                    .enabled(true)
                    .parentId(null)
                    .build());
        }

        Page<Category> page1 = categoryRepository.findAll(PageRequest.of(0, 10));
        Page<Category> page2 = categoryRepository.findAll(PageRequest.of(1, 10));

        assertEquals(10, page1.getContent().size());
        assertEquals(5, page2.getContent().size());
        assertEquals(2, page1.getTotalPages());
    }

    /**
     * Teste: Ordenação de categorias por nome
     */
    @Test
    void testCategoryOrderingByName() {
        categoryRepository.save(Category.builder().name("Zebra").enabled(true).parentId(null).build());
        categoryRepository.save(Category.builder().name("Apple").enabled(true).parentId(null).build());
        categoryRepository.save(Category.builder().name("Mango").enabled(true).parentId(null).build());

        Page<Category> sorted = categoryRepository.findAll(PageRequest.of(0, 10, Sort.by("name").ascending()));

        assertEquals("Apple", sorted.getContent().get(0).getName());
        assertEquals("Mango", sorted.getContent().get(1).getName());
        assertEquals("Zebra", sorted.getContent().get(2).getName());
    }

    /**
     * Teste: Categoria habilitada/desabilitada
     */
    @Test
    void testCategoryEnabledStatus() {
        rootCategory.setEnabled(false);
        Category saved = categoryRepository.save(rootCategory);

        assertFalse(saved.getEnabled());

        saved.setEnabled(true);
        Category updated = categoryRepository.save(saved);

        assertTrue(updated.getEnabled());
    }
}
