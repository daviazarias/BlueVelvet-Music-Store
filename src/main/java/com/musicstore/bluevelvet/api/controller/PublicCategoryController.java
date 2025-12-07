package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller para listagem pública de categorias e produtos
 * US-2100: List products within a category for the online shopper
 */
@Controller
@RequiredArgsConstructor
public class PublicCategoryController {

    private static final Integer SHOP_PAGE_SIZE = 10;
    private static final Integer PRODUCT_PAGE_SIZE = 10;
    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * Redireciona a raiz para a loja
     */
    @GetMapping("/")
    public String redirectToShop() {
        return "redirect:/shop";
    }

    /**
     * Página inicial da loja - lista categorias raiz habilitadas
     */
    @GetMapping("/shop")
    public String shopHome(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            Model model
    ) {
        Page<CategoryResponse> categories = categoryService.findAllRootsPaginated(
                PageRequest.of(page, SHOP_PAGE_SIZE, Sort.by("name").ascending())
        );

        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("breadcrumb", "Home");

        return "shop/home";
    }

    /**
     * Listagem de produtos dentro de uma categoria
     * Mostra breadcrumb, subcategorias e produtos ordenados por nome
     * US-2100: List products within a category for the online shopper
     */
    @GetMapping("/shop/category/{id}")
    public String viewCategoryProducts(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            Model model
    ) {
        try {
            CategoryResponse category = categoryService.findById(id);

            // Validar se a categoria está habilitada
            if (!category.getEnabled()) {
                return "redirect:/shop";
            }

            // Construir breadcrumb
            List<String> breadcrumb = buildBreadcrumb(category);

            // Buscar subcategorias habilitadas e ordenadas por nome
            List<CategoryResponse> enabledChildren = new ArrayList<>();
            if (category.getChildren() != null) {
                enabledChildren = category.getChildren().stream()
                        .filter(CategoryResponse::getEnabled)
                        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                        .toList();
            }

            // Buscar produtos habilitados da categoria
            Page<ProductResponse> products = productService.findByCategoryAndEnabled(id,
                    PageRequest.of(page, PRODUCT_PAGE_SIZE, Sort.by("name").ascending()));

            model.addAttribute("category", category);
            model.addAttribute("children", enabledChildren);
            model.addAttribute("breadcrumb", breadcrumb);
            model.addAttribute("products", products.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", products.getTotalPages());

            return "shop/category";
        } catch (Exception e) {
            return "redirect:/shop";
        }
    }

    /**
     * Constrói o breadcrumb para uma categoria
     * Exemplo: Home / Music / MP3
     */
    private List<String> buildBreadcrumb(CategoryResponse category) {
        List<String> breadcrumb = new ArrayList<>();
        breadcrumb.add("Home");

        // Adicionar caminho até a categoria raiz
        CategoryResponse current = category;
        List<String> path = new ArrayList<>();

        while (current != null) {
            path.add(0, current.getName());

            if (current.getParentId() != null) {
                try {
                    current = categoryService.findById(current.getParentId());
                } catch (Exception e) {
                    break;
                }
            } else {
                break;
            }
        }

        breadcrumb.addAll(path);
        return breadcrumb;
    }
}
