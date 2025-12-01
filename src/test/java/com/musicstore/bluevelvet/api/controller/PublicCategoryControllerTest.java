package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para PublicCategoryController
 * Valida a User Story US-2100: Listagem de produtos em uma categoria
 */
@SpringBootTest
@AutoConfigureMockMvc
class PublicCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    private CategoryResponse categoryResponse;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Guitarra")
                .description("Categoria de Guitarras")
                .enabled(true)
                .parentId(null)
                .image("guitarra.jpg")
                .children(new ArrayList<>())
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
     * Teste: Acessar página inicial da loja (/shop)
     */
    @Test
    void testShopHomePage() throws Exception {
        List<CategoryResponse> categories = List.of(categoryResponse);
        Page<CategoryResponse> page = new PageImpl<>(categories);

        when(categoryService.findAllRootsPaginated(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/shop"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/home"))
                .andExpect(model().attributeExists("categories"));
    }

    /**
     * Teste: US-2100 - Acessar página de categoria com produtos
     */
    @Test
    void testCategoryPageWithProducts() throws Exception {
        List<ProductResponse> products = List.of(productResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products);

        when(categoryService.findById(1L)).thenReturn(categoryResponse);
        when(productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/shop/category/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/category"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("breadcrumb"));
    }

    /**
     * Teste: US-2100 - Listar produtos habilitados apenas
     */
    @Test
    void testCategoryPageShowsOnlyEnabledProducts() throws Exception {
        ProductResponse disabledProduct = ProductResponse.builder()
                .id(2L)
                .name("Guitarra Desabilitada")
                .enabled(false)
                .categoryId(1L)
                .build();

        List<ProductResponse> products = List.of(productResponse); // Apenas habilitado
        Page<ProductResponse> productPage = new PageImpl<>(products);

        when(categoryService.findById(1L)).thenReturn(categoryResponse);
        when(productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/shop/category/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", products));
    }

    /**
     * Teste: US-2100 - Paginação de produtos (12 por página)
     */
    @Test
    void testCategoryPageProductPagination() throws Exception {
        List<ProductResponse> products = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            products.add(ProductResponse.builder()
                    .id((long) i)
                    .name("Produto " + i)
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(10)
                    .enabled(true)
                    .categoryId(1L)
                    .build());
        }

        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 12), 24);

        when(categoryService.findById(1L)).thenReturn(categoryResponse);
        when(productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/shop/category/1?page=0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalPages", 2));
    }

    /**
     * Teste: Categoria desabilitada redireciona para /shop
     */
    @Test
    void testDisabledCategoryRedirectsToShop() throws Exception {
        CategoryResponse disabledCategory = CategoryResponse.builder()
                .id(1L)
                .name("Guitarra")
                .enabled(false)
                .build();

        when(categoryService.findById(1L)).thenReturn(disabledCategory);

        mockMvc.perform(get("/shop/category/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));
    }

    /**
     * Teste: US-2100 - Breadcrumb na página de categoria
     */
    @Test
    void testCategoryPageBreadcrumb() throws Exception {
        List<ProductResponse> products = List.of(productResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products);

        when(categoryService.findById(1L)).thenReturn(categoryResponse);
        when(productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/shop/category/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumb"));
    }

    /**
     * Teste: US-2100 - Produtos ordenados por nome
     */
    @Test
    void testProductsOrderedByName() throws Exception {
        ProductResponse product1 = ProductResponse.builder()
                .id(1L)
                .name("Baixo Elétrico")
                .price(new BigDecimal("1200.00"))
                .enabled(true)
                .categoryId(1L)
                .build();

        ProductResponse product2 = ProductResponse.builder()
                .id(2L)
                .name("Guitarra Clássica")
                .price(new BigDecimal("1500.00"))
                .enabled(true)
                .categoryId(1L)
                .build();

        List<ProductResponse> products = List.of(product1, product2); // Ordenado por nome
        Page<ProductResponse> productPage = new PageImpl<>(products);

        when(categoryService.findById(1L)).thenReturn(categoryResponse);
        when(productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/shop/category/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", products));
    }

    /**
     * Teste: US-2100 - Exibir informações do produto (preço, estoque)
     */
    @Test
    void testProductDetailsDisplayed() throws Exception {
        List<ProductResponse> products = List.of(productResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products);

        when(categoryService.findById(1L)).thenReturn(categoryResponse);
        when(productService.findByCategoryAndEnabled(1L, PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/shop/category/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", products));
    }

    /**
     * Teste: Categoria não encontrada
     */
    @Test
    void testCategoryNotFound() throws Exception {
        when(categoryService.findById(999L)).thenThrow(new RuntimeException("Categoria não encontrada"));

        mockMvc.perform(get("/shop/category/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));
    }
}
