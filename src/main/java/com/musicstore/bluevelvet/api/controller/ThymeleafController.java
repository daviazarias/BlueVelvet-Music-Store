package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public static final String REDIRECT_AUTHENTICATION_ERROR = "redirect:/login";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String CATEGORIES = "categories";

    public static final String ADMIN = "ROLE_ADMINISTRATOR";
    public static final String EDITOR = "ROLE_EDITOR";
    public static final String SHIPPER = "ROLE_SHIPPING_MANAGER";
    public static final String SALESPERSON = "ROLE_SALES_MANAGER";
    public static final String ASSISTANT = "ROLE_ASSISTANT";

    private static final Integer DEFAULT_DASHBOARD_PAGE_SIZE = 10;  // US-2032: 10 categorias por página no dashboard
    private static final Integer DEFAULT_LIST_PAGE_SIZE = 5;      // US-0907: 5 categorias raiz por página na listagem
    private final CategoryService service;
    private final FileStorageService fileStorageService;


    // ============== DASHBOARD ==============

    @GetMapping("/dashboard")
    public String categoriesDashboard(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            @RequestParam(name = "asc", defaultValue = "true") Boolean asc,
            @RequestParam(name = "search", required = false) String search,
            Model model,
            Authentication authentication
    ) {

        if(!authenticateUser(authentication, List.of(ADMIN, SALESPERSON, SHIPPER)))
            return REDIRECT_AUTHENTICATION_ERROR;

        // Adiciona informações do usuário autenticado
        addUserInformations(model, authentication);

        Page<CategoryResponse> responsePage = service.findAllRoots(
                PageRequest.of(page, DEFAULT_DASHBOARD_PAGE_SIZE,
                        Sort.by(asc ? Sort.Order.asc(sort) : Sort.Order.desc(sort))));

        model.addAttribute(CATEGORIES, responsePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", responsePage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);
        model.addAttribute("asc", asc);

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

        if(!authenticateUser(authentication, List.of(ADMIN, EDITOR)))
            return REDIRECT_DASHBOARD;

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
            @RequestParam(name = "asc", defaultValue = "true") Boolean asc,
            Model model,
            Authentication authentication
    ) {

        if(!authenticateUser(authentication, List.of(ADMIN, EDITOR)))
            return REDIRECT_AUTHENTICATION_ERROR;

        addUserInformations(model, authentication);

        Page<CategoryResponse> responsePage = service.findAllRootsWithOrderedChildren(
                PageRequest.of(page, DEFAULT_LIST_PAGE_SIZE,
                        Sort.by(asc ? Sort.Order.asc(sort) : Sort.Order.desc(sort)))
        );

        model.addAttribute(CATEGORIES, responsePage);
        model.addAttribute("sort", sort);
        model.addAttribute("asc", asc);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", responsePage.getTotalPages());
        return "list";
    }

    // ============== CREATE CATEGORY ==============

    @GetMapping("/create-category")
    public String createCategoryForm(Model model, Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN)))
            return REDIRECT_AUTHENTICATION_ERROR;

        List<CategoryResponse> responseList = service.findAllRoots();
        model.addAttribute("parentCategories", responseList);
        model.addAttribute("category", new CategoryRequest());
        model.addAttribute("viewMode", "create");
        return "form-category";
    }

    @PostMapping("/category")
    public String createCategory(@ModelAttribute CategoryRequest request,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN)))
            return REDIRECT_AUTHENTICATION_ERROR;

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
    public String editCategoryForm(@PathVariable Long id,
                                   Model model,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN)))
            return REDIRECT_AUTHENTICATION_ERROR;

        try {
            CategoryResponse category = service.findById(id);
            List<CategoryResponse> parentCategories = service.findAllRoots();

            model.addAttribute("category", category);
            model.addAttribute("parentCategories", parentCategories);
            model.addAttribute("viewMode", "edit");

            // Busca categoria pai se existir
            if (category.getParentId() != null) {
                try {
                    CategoryResponse parent = service.findById(category.getParentId());
                    model.addAttribute("parentCategory", parent);
                } catch (Exception e) {
                    model.addAttribute("parentCategory", null);
                }
            }

            return "form-category";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Categoria não encontrada");
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/category/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @ModelAttribute CategoryRequest request,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN)))
            return REDIRECT_AUTHENTICATION_ERROR;

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
    public String viewCategory(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN, EDITOR)))
            return REDIRECT_AUTHENTICATION_ERROR;

        try {
            CategoryResponse category = service.findById(id);
            List<CategoryResponse> parentCategories = service.findAllRoots();

            model.addAttribute(CATEGORY, category);
            model.addAttribute("parentCategories", parentCategories);
            model.addAttribute("viewMode", "view");

            // Busca categoria pai se existir
            if (category.getParentId() != null) {
                fatherCategory(model, category);
            }

            return "form-category";
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
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN, EDITOR)))
            return REDIRECT_AUTHENTICATION_ERROR;

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
    public org.springframework.http.ResponseEntity<byte[]> exportCategoriesCSV(Authentication authentication) {

        if(!authenticateUser(authentication, List.of(ADMIN, EDITOR)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            String csvContent = service.exportToCSV();
            String fileName = service.generateCSVFileName();

            byte[] csvBytes = csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            return org.springframework.http.ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", "text/csv; charset=UTF-8")
                    .body(csvBytes);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void addUserInformations(Model model, Authentication authentication) {
        if (authentication != null) {
            String userName = "Usuário";
            if (authentication.getPrincipal() instanceof com.musicstore.bluevelvet.domain.service.CustomUserDetails) {
                com.musicstore.bluevelvet.domain.service.CustomUserDetails userDetails =
                        (com.musicstore.bluevelvet.domain.service.CustomUserDetails) authentication.getPrincipal();
                userName = userDetails.getName();
            }
            model.addAttribute("userName", userName);
            String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .findFirst()
                    .orElse("USER");
            model.addAttribute("role", role.replace("ROLE_", ""));
        }
    }

    private Boolean authenticateUser(Authentication authentication, List<String> allowedRoles){
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> allowedRoles.contains(a.getAuthority()));
    }
}
