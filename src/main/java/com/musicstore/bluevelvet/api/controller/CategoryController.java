package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2 @RestController @RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", description = "Get a product category from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id){
        log.info("Request received to fetch a category by id {}", id);

        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all product categories from the Blue Velvet Music Store")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        log.info("Request received to fetch all categories");
        return ResponseEntity.ok(service.findAllRoots(pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a new product category for the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        log.info("Request received to create a new category. The request is {}", request);

        return ResponseEntity.ok(service.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by id", description = "Update a product category from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> updateCategoryById(@PathVariable Long id, @RequestBody CategoryRequest request){
        log.info("Request received to update the category {} to {}", id, request);

        return ResponseEntity.ok(service.updateCategory(id,request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id", description = "Delete a product category from the Blue Velvet Music Store")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id){
        log.info("Request received to delete a category by id {}", id);
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update by id", description = "Update only selected fields of a category from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> patchCategoryById(@PathVariable Long id, @RequestBody CategoryRequest request){
        log.info("Request received to partially update a category by id {} to {}", id, request);

        return ResponseEntity.ok(service.partiallyUpdateCategory(id, request));
    }

}
