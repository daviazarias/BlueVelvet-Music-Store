package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ThymeleafController {

    public static final String CATEGORY = "category";
    public static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String CATEGORIES = "categories";
    private static final Integer DEFAULT_DASHBOARD_PAGE_SIZE = 10;
    private static final Integer DEFAULT_LIST_PAGE_SIZE = 5;
    private final CategoryService service;
    private final FileStorageService fileStorageService;

    @GetMapping("/")
    public String getHomePage(){
        return "index";
    }

    // ============== DASHBOARD ==============

    @GetMapping("/dashboard")
    public String categoriesDashboard(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            @RequestParam(name = "search", required = false) String search,
            Model model,
            Authentication authentication
    ) {
        // Adiciona informações do usuário autenticado
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .findFirst()
                    .orElse("USER");
            model.addAttribute("role", role.replace("ROLE_", ""));
        }

        Page<CategoryResponse> responsePage = service.findAllRoots(
                PageRequest.of(page, DEFAULT_DASHBOARD_PAGE_SIZE, Sort.by(sort)));

        model.addAttribute(CATEGORIES, responsePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", responsePage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);

        return "dashboard";
    }

    /**
     * Busca categorias por nome
     */
    @GetMapping("/dashboard/search")
    public String searchCategories(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            Model model,
            Authentication authentication
    ) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .findFirst()
                    .orElse("USER");
            model.addAttribute("role", role.replace("ROLE_", ""));
        }

        Page<CategoryResponse> responsePage;

        if (query != null && !query.trim().isEmpty()) {
            responsePage = service.searchCategories(query,
                    PageRequest.of(page, DEFAULT_DASHBOARD_PAGE_SIZE, Sort.by(sort)));
        } else {
            responsePage = service.findAllRoots(
                    PageRequest.of(page, DEFAULT_DASHBOARD_PAGE_SIZE, Sort.by(sort)));
        }

        model.addAttribute(CATEGORIES, responsePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", responsePage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("search", query);

        return "dashboard";
    }


    // ============== LIST ==============

    @GetMapping("/list")
    public String categoriesList(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            Model model
    ) {
        Page<CategoryResponse> responsePage = service.findAllRootsWithOrderedChildren(
                PageRequest.of(page, DEFAULT_LIST_PAGE_SIZE, Sort.by(sort))
        );

        model.addAttribute(CATEGORIES, responsePage);
        model.addAttribute("sort", sort);
        return "list";
    }

    // ============== CREATE CATEGORY ==============

    @GetMapping("/create-category")
    public String createCategoryForm(Model model) {
        List<CategoryResponse> responseList = service.findAllRoots();
        model.addAttribute("parentCategories", responseList);
        model.addAttribute(CATEGORY, new CategoryRequest());
        return "create-category";
    }

    @PostMapping("/category")
    public String createCategory(@ModelAttribute CategoryRequest request,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Processar upload de imagem
            if (imageFile != null && !imageFile.isEmpty()) {
                String savedFileName = fileStorageService.saveFile(imageFile);
                request.setImage(savedFileName);
            }

            request.setIsRoot(request.getParentId() == null);
            service.createCategory(request);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Categoria criada com sucesso!");
            return REDIRECT_DASHBOARD;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Erro ao criar categoria: " + e.getMessage());
            return "redirect:/create-category";
        }
    }

    // ============== EDIT CATEGORY ==============

    @GetMapping("/category/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CategoryResponse category = service.findById(id);
            List<CategoryResponse> parentCategories = service.findAllRoots();

            model.addAttribute(CATEGORY, category);
            model.addAttribute("parentCategories", parentCategories);
            return "edit-category";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Categoria não encontrada");
            return REDIRECT_DASHBOARD;
        }
    }

    @PostMapping("/category/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @ModelAttribute CategoryRequest request,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Processar upload de nova imagem
            if (imageFile != null && !imageFile.isEmpty()) {
                // Busca categoria antiga para deletar imagem anterior
                CategoryResponse oldCategory = service.findById(id);
                if (oldCategory.getImage() != null && !oldCategory.getImage().isEmpty()) {
                    fileStorageService.deleteFile(oldCategory.getImage());
                }

                String savedFileName = fileStorageService.saveFile(imageFile);
                request.setImage(savedFileName);
            }

            request.setIsRoot(request.getParentId() == null);
            service.updateCategory(id, request);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Categoria atualizada com sucesso!");
            return REDIRECT_DASHBOARD;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Erro ao atualizar categoria: " + e.getMessage());
            return "redirect:/category/" + id + "/edit";
        }
    }

    // ============== VIEW CATEGORY ==============

    @GetMapping("/category/{id}")
    public String viewCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CategoryResponse category = service.findById(id);
            model.addAttribute(CATEGORY, category);

            // Busca categoria pai se existir
            if (category.getParentId() != null) {
                fatherCategory(model, category);
            }

            return "view-category";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Categoria não encontrada");
            return REDIRECT_DASHBOARD;
        }
    }

    private void fatherCategory(Model model, CategoryResponse category) {
        try {
            CategoryResponse parent = service.findById(category.getParentId());
            model.addAttribute("parentCategory", parent);
        } catch (Exception e) {
            model.addAttribute("parentCategory", null);
        }
    }

    // ============== DELETE CATEGORY ==============

    @PostMapping("/category/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Categoria deletada com sucesso!");
            return REDIRECT_DASHBOARD + "?deleted=success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Erro ao deletar categoria: " + e.getMessage());
            return REDIRECT_DASHBOARD + "?deleted=error";
        }
    }

    // ============== RESET CATEGORIES ==============

    @PostMapping("/category/reset")
    public String resetCategories(RedirectAttributes redirectAttributes) {
        try {
            service.deleteAllAndResetInitial();
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Categorias resetadas para estado inicial!");
            return REDIRECT_DASHBOARD + "?reset=success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Erro ao resetar categorias: " + e.getMessage());
            return REDIRECT_DASHBOARD + "?reset=error";
        }
    }

    // ============== EXPORT CSV ==============

    @GetMapping("/category/export/csv")
    public String exportCategoriesCSV(Model model) {
        List<CategoryResponse> categories = service.findAllRoots();
        // Lógica de export será implementada em um service separado
        model.addAttribute(CATEGORIES, categories);
        return REDIRECT_DASHBOARD;
    }
}
