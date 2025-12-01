package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ThymeleafController
 * Valida as rotas e funcionalidades da dashboard
 */
@SpringBootTest
@AutoConfigureMockMvc
class ThymeleafControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private FileStorageService fileStorageService;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Rock")
                .description("Categoria de Rock")
                .enabled(true)
                .parentId(null)
                .image("rock.jpg")
                .children(new ArrayList<>())
                .build();
    }

    /**
     * Teste: Redirecionar rota raiz para /shop
     */
    @Test
    void testRootPathRedirectsToShop() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));
    }

    /**
     * Teste: Acessar dashboard sem autenticação (deve redirecionar para login)
     */
    @Test
    void testDashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Teste: Acessar dashboard com autenticação
     */
    @Test
    @WithMockUser(username = "admin@bluevelvet.com", roles = "ADMINISTRATOR")
    void testDashboardWithAuthentication() throws Exception {
        List<CategoryResponse> categories = List.of(categoryResponse);
        Page<CategoryResponse> page = new PageImpl<>(categories);

        when(categoryService.findAllRoots(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("userName"));
    }

    /**
     * Teste: Dashboard com paginação
     */
    @Test
    @WithMockUser(username = "admin@bluevelvet.com", roles = "ADMINISTRATOR")
    void testDashboardPagination() throws Exception {
        List<CategoryResponse> categories = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            categories.add(CategoryResponse.builder()
                    .id((long) i)
                    .name("Category " + i)
                    .enabled(true)
                    .build());
        }

        Page<CategoryResponse> page = new PageImpl<>(categories, PageRequest.of(0, 10), 25);

        when(categoryService.findAllRoots(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/dashboard?page=0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 3));
    }

    /**
     * Teste: Buscar categorias por nome
     */
    @Test
    @WithMockUser(username = "admin@bluevelvet.com", roles = "ADMINISTRATOR")
    void testSearchCategories() throws Exception {
        List<CategoryResponse> searchResults = List.of(categoryResponse);
        Page<CategoryResponse> page = new PageImpl<>(searchResults);

        when(categoryService.searchByName("Rock", PageRequest.of(0, 10))).thenReturn(page);

        mockMvc.perform(get("/dashboard/search?q=Rock"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    /**
     * Teste: Ordenação por nome
     */
    @Test
    @WithMockUser(username = "admin@bluevelvet.com", roles = "ADMINISTRATOR")
    void testDashboardSortingByName() throws Exception {
        List<CategoryResponse> categories = List.of(categoryResponse);
        Page<CategoryResponse> page = new PageImpl<>(categories);

        when(categoryService.findAllRoots(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/dashboard?sort=name"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sort", "name"));
    }

    /**
     * Teste: Ordenação por ID
     */
    @Test
    @WithMockUser(username = "admin@bluevelvet.com", roles = "ADMINISTRATOR")
    void testDashboardSortingById() throws Exception {
        List<CategoryResponse> categories = List.of(categoryResponse);
        Page<CategoryResponse> page = new PageImpl<>(categories);

        when(categoryService.findAllRoots(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/dashboard?sort=id"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sort", "id"));
    }
}
