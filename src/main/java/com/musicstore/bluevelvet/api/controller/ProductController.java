package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.ProductRequest;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.musicstore.bluevelvet.domain.service.FileStorageService;
import com.musicstore.bluevelvet.domain.service.CategoryService;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Integer PAGE_SIZE = 10;
    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ) {
        Page<ProductResponse> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchByName(search,
                    PageRequest.of(page, PAGE_SIZE, Sort.by(sort)));
        } else {
            products = productService.findAll(
                    PageRequest.of(page, PAGE_SIZE, Sort.by(sort)));
        }

        model.addAttribute("products", products.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);

        return "products/list";
    }

    @GetMapping("/create")
    public String createProductForm(Model model) {
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryService.findAllRoots());
        model.addAttribute("viewMode", "create");
        return "products/form";
    }

    @PostMapping
    public String createProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String savedFileName = fileStorageService.saveFile(imageFile);
                request.setImage(savedFileName);
            }

            productService.createProduct(request);
            redirectAttributes.addFlashAttribute("successMessage", "Produto criado com sucesso!");
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar produto: " + e.getMessage());
            return "redirect:/products/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductResponse product = productService.findById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.findAllRoots());
            model.addAttribute("viewMode", "edit");
            return "products/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Produto não encontrado");
            return "redirect:/products";
        }
    }

    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                ProductResponse oldProduct = productService.findById(id);
                if (oldProduct.getImage() != null && !oldProduct.getImage().isEmpty()) {
                    fileStorageService.deleteFile(oldProduct.getImage());
                }

                String savedFileName = fileStorageService.saveFile(imageFile);
                request.setImage(savedFileName);
            }

            productService.updateProduct(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar produto: " + e.getMessage());
            return "redirect:/products/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produto deletado com sucesso!");
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar produto: " + e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductResponse product = productService.findById(id);
            model.addAttribute("product", product);
            model.addAttribute("viewMode", "view");
            return "products/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Produto não encontrado");
            return "redirect:/products";
        }
    }
}
