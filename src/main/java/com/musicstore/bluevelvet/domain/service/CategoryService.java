package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.converter.CategoryConverter;
import com.musicstore.bluevelvet.domain.exception.CategoryNotFoundException;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.entity.Product;
import com.musicstore.bluevelvet.infrastructure.repository.CategoryRepository;
import com.musicstore.bluevelvet.infrastructure.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @Log4j2 @RequiredArgsConstructor
public class CategoryService {

    public static final String UNABLE_TO_FIND_A_CATEGORY_WITH_ID_D = "Unable to find a category with id %d";
    private final CategoryRepository repository;
    private final ProductRepository productRepository;

    public CategoryResponse findById(Long id) {

        Category category = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(UNABLE_TO_FIND_A_CATEGORY_WITH_ID_D.formatted(id))
        );

        return CategoryConverter.convertToCategoryResponse(category);
    }

    public Page<CategoryResponse> findAllRoots(Pageable pageable){
        return repository
                .findAll(pageable)
                .map(CategoryConverter::convertToCategoryResponse);
    }

    public Page<CategoryResponse> findAllRootsWithOrderedChildren(Pageable pageable){
        Page<Category> parentCategories = repository.findByIsRootIsTrue(pageable);

        for(var parentCategory : parentCategories)
            parentCategory.setChildren(
                    repository.findByParent(parentCategory, pageable.getSort())
            );

        return parentCategories.map(CategoryConverter::convertToCategoryResponse);
    }

    public List<CategoryResponse> findAllRoots() {
        return repository
                .findByIsRootIsTrue()
                .stream()
                .map(CategoryConverter::convertToCategoryResponse)
                .toList();
    }

    public void deleteById(Long id){

        Category category = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(UNABLE_TO_FIND_A_CATEGORY_WITH_ID_D.formatted(id))
        );

        // Validar se a categoria tem subcategorias
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("Não é possível deletar uma categoria que possui subcategorias.");
        }

        // Validar se a categoria tem produtos
        Page<Product> products = productRepository.findByCategory(category, PageRequest.of(0, 1));

        if (!products.isEmpty()) {
            throw new IllegalArgumentException("Não é possível deletar uma categoria que possui produtos associados.");
        }

        repository.deleteById(id);
    }

    public CategoryResponse createCategory(CategoryRequest request){

        Category category = CategoryConverter.convertToCategory(request);

        fillJoinedAttributes(request, category);

        return CategoryConverter.convertToCategoryResponse(repository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request){

        Category oldCategory = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(UNABLE_TO_FIND_A_CATEGORY_WITH_ID_D.formatted(id))
        );

        Category updatedCategory = CategoryConverter.convertToCategory(request);
        updatedCategory.setId(oldCategory.getId());
        fillJoinedAttributes(request,updatedCategory);

        return CategoryConverter.convertToCategoryResponse(repository.save(updatedCategory));
    }

    public CategoryResponse partiallyUpdateCategory(Long id, CategoryRequest request){
        Category category = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(UNABLE_TO_FIND_A_CATEGORY_WITH_ID_D.formatted(id))
        );

        if(request.getName() != null)
            category.setName(request.getName());

        if(request.getImage() != null)
            category.setImage(request.getImage());

        if(request.getEnabled() != null)
            category.setEnabled(request.getEnabled());

        fillJoinedAttributes(request, category);

        return CategoryConverter.convertToCategoryResponse(repository.save(category));
    }

    private void fillJoinedAttributes(CategoryRequest request, Category category) {
        if(request.getParentId() != null){
            Category parent = repository.findById(request.getParentId()).orElseThrow(() ->
                    new CategoryNotFoundException("Unable to find category parent with id %d".formatted(request.getParentId())));
            category.setParent(parent);
        }
    }

    // ============== NOVOS MÉTODOS A ADICIONAR ==============

    /**
     * Encontra todas as categorias raiz com paginação
     */
    public Page<CategoryResponse> findAllRootsPaginated(Pageable pageable) {
        return repository
                .findByIsRootIsTrue(pageable)
                .map(CategoryConverter::convertToCategoryResponse);
    }

    /**
     * Busca categorias por nome (LIKE)
     */
    public Page<CategoryResponse> searchCategories(String name, Pageable pageable) {
        return repository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(CategoryConverter::convertToCategoryResponse);
    }

    /**
     * Deleta todas as categorias e recria as 10 iniciais
     */
    public void deleteAllAndResetInitial() {
        repository.deleteAll();
        createInitialCategories();
    }

    /**
     * Cria as 10 categorias iniciais
     */
    private void createInitialCategories() {
        String[] initialCategories = {
                "T-Shirts", "Vinyl", "CDs", "MP3", "Books",
                "Acoustic Guitar", "Electric Guitar", "Bass", "Drums", "Accessories"
        };

        for (String name : initialCategories) {
            Category category = Category.builder()
                    .name(name)
                    .isRoot(true)
                    .enabled(true)
                    .build();
            repository.save(category);
        }
    }

    /**
     * Exporta categorias em formato CSV
     */
    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("id,name\n");

        List<Category> categories = repository.findByIsRootIsTrue();
        for (Category category : categories) {
            csv.append(category.getId()).append(",").append(category.getName()).append("\n");

            // Adiciona subcategorias com indentação
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                addChildrenToCSV(csv, category.getChildren(), "  ");
            }
        }

        return csv.toString();
    }

    /**
     * Auxilia na indentação de subcategorias no CSV
     */
    private void addChildrenToCSV(StringBuilder csv, List<Category> children, String indent) {
        for (Category child : children) {
            csv.append(child.getId()).append(",").append(indent).append(child.getName()).append("\n");

            if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                addChildrenToCSV(csv, child.getChildren(), indent + "  ");
            }
        }
    }

    public String generateCSVFileName() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "categories_" + now.format(formatter) + ".csv";
    }

}
