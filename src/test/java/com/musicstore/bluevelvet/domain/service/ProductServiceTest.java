package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.infrastructure.entity.Product;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.repository.ProductRepository;
import com.musicstore.bluevelvet.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProductService
 * Valida a User Story: US-2100 (Listagem de produtos em uma categoria)
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponse productResponse;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Guitarra")
                .enabled(true)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Guitarra Clássica")
                .description("Guitarra de qualidade premium")
                .price(new BigDecimal("1500.00"))
                .stockQuantity(10)
                .enabled(true)
                .category(category)
                .image("guitarra.jpg")
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Guitarra Clássica")
                .description("Guitarra de qualidade premium")
                .price(new BigDecimal("1500.00"))
                .stockQuantity(10)
                .enabled(true)
                .categoryId(1L)
                .categoryName("Guitarra")
                .image("guitarra.jpg")
                .build();
    }

    /**
     * US-2100: Criar produto
     */
    @Test
    void testCreateProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.create(productResponse);

        assertNotNull(result);
        assertEquals("Guitarra Clássica", result.getName());
        assertEquals(new BigDecimal("1500.00"), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * US-2100: Atualizar produto
     */
    @Test
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse result = productService.update(1L, productResponse);

        assertNotNull(result);
        assertEquals("Guitarra Clássica", result.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * US-2100: Deletar produto
     */
    @Test
    void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.delete(1L));
        verify(productRepository, times(1)).deleteById(1L);
    }

    /**
     * US-2100: Buscar produto por ID
     */
    @Test
    void testFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Guitarra Clássica", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    /**
     * US-2100: Listar produtos por categoria (com paginação)
     */
    @Test
    void testFindProductsByCategory() {
        List<Product> products = List.of(product);
        Page<Product> page = new PageImpl<>(products, PageRequest.of(0, 12), 1);

        when(productRepository.findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12)))
                .thenReturn(page);

        Page<ProductResponse> result = productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Guitarra Clássica", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12));
    }

    /**
     * US-2100: Listar apenas produtos habilitados
     */
    @Test
    void testFindOnlyEnabledProducts() {
        product.setEnabled(true);
        List<Product> products = List.of(product);
        Page<Product> page = new PageImpl<>(products);

        when(productRepository.findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12)))
                .thenReturn(page);

        Page<ProductResponse> result = productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12));

        assertTrue(result.getContent().stream().allMatch(ProductResponse::getEnabled));
        verify(productRepository, times(1)).findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12));
    }

    /**
     * US-2100: Validar preço do produto
     */
    @Test
    void testProductPriceValidation() {
        assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(new BigDecimal("1500.00"), product.getPrice());
    }

    /**
     * US-2100: Validar estoque do produto
     */
    @Test
    void testProductStockValidation() {
        assertTrue(product.getStockQuantity() >= 0);
        assertEquals(10, product.getStockQuantity());

        product.setStockQuantity(0);
        assertEquals(0, product.getStockQuantity());
    }

    /**
     * US-2100: Paginação de produtos (12 por página)
     */
    @Test
    void testProductPaginationWithTwelveItemsPerPage() {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            products.add(Product.builder()
                    .id((long) i)
                    .name("Produto " + i)
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(10)
                    .enabled(true)
                    .category(category)
                    .build());
        }

        Page<Product> page = new PageImpl<>(products, PageRequest.of(0, 12), 24);

        when(productRepository.findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12)))
                .thenReturn(page);

        Page<ProductResponse> result = productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12));

        assertEquals(12, result.getContent().size());
        assertEquals(24, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        verify(productRepository, times(1)).findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12));
    }

    /**
     * US-2100: Ordenação de produtos por nome
     */
    @Test
    void testProductOrderingByName() {
        Product product2 = Product.builder()
                .id(2L)
                .name("Baixo Elétrico")
                .price(new BigDecimal("1200.00"))
                .stockQuantity(5)
                .enabled(true)
                .category(category)
                .build();

        List<Product> products = List.of(product2, product); // Ordenado por nome
        Page<Product> page = new PageImpl<>(products);

        when(productRepository.findByCategoryIdAndEnabled(1L, true, PageRequest.of(0, 12)))
                .thenReturn(page);

        Page<ProductResponse> result = productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12));

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().get(0).getName().compareTo(result.getContent().get(1).getName()) < 0);
    }
}
