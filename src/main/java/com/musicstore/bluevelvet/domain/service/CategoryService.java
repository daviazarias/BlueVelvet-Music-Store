package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.converter.CategoryConverter;
import com.musicstore.bluevelvet.domain.exception.CategoryNotFoundException;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @Log4j2 @RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryResponse findById(Long id) {

        Category category = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException("Unable to find a category with id %d".formatted(id))
        );

        return CategoryConverter.convertToCategoryResponse(category);
    }

    public Page<CategoryResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(CategoryConverter::convertToCategoryResponse);
    }

    public List<CategoryResponse> findAll() {
        return repository.findAll().stream().map(CategoryConverter::convertToCategoryResponse).toList();
    }

    public void deleteById(Long id){

        repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException("Unable to find a category with id %d".formatted(id))
        );

        repository.deleteById(id);
    }

    public CategoryResponse createCategory(CategoryRequest request){

        Category category = CategoryConverter.convertToCategory(request);

        fillJoinedAttributes(request, category);

        return CategoryConverter.convertToCategoryResponse(repository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request){

        Category oldCategory = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException("Unable to find a category with id %d".formatted(id))
        );

        Category updatedCategory = CategoryConverter.convertToCategory(request);
        updatedCategory.setId(oldCategory.getId());
        fillJoinedAttributes(request,updatedCategory);

        return CategoryConverter.convertToCategoryResponse(repository.save(updatedCategory));
    }

    public CategoryResponse partiallyUpdateCategory(Long id, CategoryRequest request){
        Category category = repository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException("Unable to find a category with id %d".formatted(id))
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
}
