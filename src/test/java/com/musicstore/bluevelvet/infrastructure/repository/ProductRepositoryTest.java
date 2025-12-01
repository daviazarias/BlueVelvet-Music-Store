package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.Product;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para ProductRepository
 * Valida as operações de banco de dados para produtos
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .name("Instrumentos")
                .enabled(true)
                .build();
        categoryRepository.save(category);

        product = Product.builder()
                .name("Guitarra Clássica")
                .description("Guitarra de qualidade")
                .price(new BigDecimal("1500.00"))
                .stockQuantity(10)
                .enabled(true)
                .category(category)
                .build();
    }

    /**
     * Teste: Salvar produto
     */
    @Test
    void testSaveProduct() {
        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals("Guitarra Clássica", saved.getName());
        assertEquals(new BigDecimal("1500.00"), saved.getPrice());
        assertEquals(10, saved.getStockQuantity());
    }

    /**
     * Teste: Buscar produto por ID
     */
    @Test
    void testFindProductById() {
        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Guitarra Clássica", found.get().getName());
    }

    /**
     * Teste: Buscar produto por ID (não encontrado)
     */
    @Test
    void testFindProductByIdNotFound() {
        Optional<Product> found = productRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    /**
     * Teste: Listar produtos por categoria
     */
    @Test
    void testFindProductsByCategory() {
        productRepository.save(product);

        Product product2 = Product.builder()
                .name("Baixo Elétrico")
                .price(new BigDecimal("1200.00"))
                .stockQuantity(5)
                .enabled(true)
                .category(category)
                .build();
        productRepository.save(product2);

        Page<Product> results = productRepository.findByCategoryId(category.getId(), PageRequest.of(0, 10));

        assertEquals(2, results.getContent().size());
    }

    /**
     * Teste: Listar apenas produtos habilitados de uma categoria
     */
    @Test
    void testFindEnabledProductsByCategory() {
        productRepository.save(product);

        Product disabledProduct = Product.builder()
                .name("Produto Desabilitado")
                .price(new BigDecimal("100.00"))
                .stockQuantity(0)
                .enabled(false)
                .category(category)
                .build();
        productRepository.save(disabledProduct);

        Page<Product> results = productRepository.findByCategoryIdAndEnabled(category.getId(), true, PageRequest.of(0, 10));

        assertEquals(1, results.getContent().size());
        assertTrue(results.getContent().get(0).getEnabled());
    }

    /**
     * Teste: Atualizar produto
     */
    @Test
    void testUpdateProduct() {
        Product saved = productRepository.save(product);
        saved.setName("Guitarra Atualizada");
        saved.setPrice(new BigDecimal("1800.00"));
        saved.setStockQuantity(20);

        Product updated = productRepository.save(saved);

        assertEquals("Guitarra Atualizada", updated.getName());
        assertEquals(new BigDecimal("1800.00"), updated.getPrice());
        assertEquals(20, updated.getStockQuantity());
    }

    /**
     * Teste: Deletar produto
     */
    @Test
    void testDeleteProduct() {
        Product saved = productRepository.save(product);
        Long id = saved.getId();

        productRepository.deleteById(id);

        Optional<Product> found = productRepository.findById(id);
        assertFalse(found.isPresent());
    }

    /**
     * Teste: Paginação de produtos (12 por página)
     */
    @Test
    void testProductPagination() {
        for (int i = 1; i <= 25; i++) {
            productRepository.save(Product.builder()
                    .name("Produto " + i)
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(10)
                    .enabled(true)
                    .category(category)
                    .build());
        }

        Page<Product> page1 = productRepository.findByCategoryId(category.getId(), PageRequest.of(0, 12));
        Page<Product> page2 = productRepository.findByCategoryId(category.getId(), PageRequest.of(1, 12));

        assertEquals(12, page1.getContent().size());
        assertEquals(12, page2.getContent().size());
        assertEquals(3, page1.getTotalPages());
    }

    /**
     * Teste: Ordenação de produtos por nome
     */
    @Test
    void testProductOrderingByName() {
        productRepository.save(Product.builder()
                .name("Zebra Guitarra")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .enabled(true)
                .category(category)
                .build());

        productRepository.save(Product.builder()
                .name("Apple Guitarra")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .enabled(true)
                .category(category)
                .build());

        Page<Product> sorted = productRepository.findByCategoryId(category.getId(),
                PageRequest.of(0, 10, Sort.by("name").ascending()));

        assertEquals("Apple Guitarra", sorted.getContent().get(0).getName());
        assertEquals("Zebra Guitarra", sorted.getContent().get(1).getName());
    }

    /**
     * Teste: Validar preço do produto
     */
    @Test
    void testProductPriceValidation() {
        Product saved = productRepository.save(product);

        assertTrue(saved.getPrice().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(new BigDecimal("1500.00"), saved.getPrice());
    }

    /**
     * Teste: Validar estoque do produto
     */
    @Test
    void testProductStockValidation() {
        Product saved = productRepository.save(product);

        assertTrue(saved.getStockQuantity() >= 0);
        assertEquals(10, saved.getStockQuantity());

        saved.setStockQuantity(0);
        Product updated = productRepository.save(saved);

        assertEquals(0, updated.getStockQuantity());
    }

    /**
     * Teste: Produto habilitado/desabilitado
     */
    @Test
    void testProductEnabledStatus() {
        product.setEnabled(false);
        Product saved = productRepository.save(product);

        assertFalse(saved.getEnabled());

        saved.setEnabled(true);
        Product updated = productRepository.save(saved);

        assertTrue(updated.getEnabled());
    }
}
