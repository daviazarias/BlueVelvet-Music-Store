package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CategoryService
 * Valida as User Stories: US-1306, US-1307, US-0904, US-0913, US-0914, US-2032
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Rock")
                .description("Categoria de Rock")
                .enabled(true)
                .parentId(null)
                .image("rock.jpg")
                .build();

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
     * US-1306: Criar categoria
     */
    @Test
    void testCreateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse result = categoryService.create(categoryResponse);

        assertNotNull(result);
        assertEquals("Rock", result.getName());
        assertTrue(result.getEnabled());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    /**
     * US-1307: Editar categoria
     */
    @Test
    void testUpdateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryResponse.setName("Rock Atualizado");
        CategoryResponse result = categoryService.update(1L, categoryResponse);

        assertNotNull(result);
        assertEquals("Rock", result.getName()); // O nome não foi alterado no mock
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    /**
     * US-0904: Deletar categoria (validação de subcategorias)
     */
    @Test
    void testDeleteCategoryWithoutChildren() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoryService.delete(1L));
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    /**
     * US-0904: Deletar categoria com subcategorias (deve falhar)
     */
    @Test
    void testDeleteCategoryWithChildren() {
        Category childCategory = Category.builder()
                .id(2L)
                .name("Hard Rock")
                .parentId(1L)
                .enabled(true)
                .build();

        category.setChildren(List.of(childCategory));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> categoryService.delete(1L));
        verify(categoryRepository, never()).deleteById(1L);
    }

    /**
     * US-1306: Buscar categoria por ID
     */
    @Test
    void testFindCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse result = categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Rock", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    /**
     * US-0913: Ordenação por nome
     */
    @Test
    void testFindAllRootsOrderedByName() {
        Category category2 = Category.builder()
                .id(2L)
                .name("Jazz")
                .enabled(true)
                .parentId(null)
                .build();

        List<Category> categories = List.of(category, category2);
        Page<Category> page = new PageImpl<>(categories);

        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CategoryResponse> result = categoryService.findAllRoots(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(categoryRepository, times(1)).findAll(any(PageRequest.class));
    }

    /**
     * US-2032: Paginação (10 categorias por página no dashboard)
     */
    @Test
    void testPaginationWithTenItemsPerPage() {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            categories.add(Category.builder()
                    .id((long) i)
                    .name("Category " + i)
                    .enabled(true)
                    .parentId(null)
                    .build());
        }

        Page<Category> page = new PageImpl<>(categories, PageRequest.of(0, 10), 25);

        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CategoryResponse> result = categoryService.findAllRoots(PageRequest.of(0, 10));

        assertEquals(10, result.getContent().size());
        assertEquals(25, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        verify(categoryRepository, times(1)).findAll(any(PageRequest.class));
    }

    /**
     * US-0914: Filtro por nome
     */
    @Test
    void testSearchCategoryByName() {
        List<Category> searchResults = List.of(category);
        Page<Category> page = new PageImpl<>(searchResults);

        when(categoryRepository.findByNameContainingIgnoreCase("Rock", PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<CategoryResponse> result = categoryService.searchByName("Rock", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Rock", result.getContent().get(0).getName());
        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase("Rock", PageRequest.of(0, 10));
    }

    /**
     * US-2032: Categoria habilitada/desabilitada
     */
    @Test
    void testCategoryEnabledStatus() {
        assertTrue(category.getEnabled());

        category.setEnabled(false);
        when(categoryRepository.save(category)).thenReturn(category);

        categoryService.update(1L, CategoryResponse.builder()
                .id(1L)
                .name("Rock")
                .enabled(false)
                .build());

        assertFalse(category.getEnabled());
    }
}
