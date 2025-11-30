package com.musicstore.bluevelvet.domain.converter;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.infrastructure.entity.Category;

import java.util.List;

public class CategoryConverter {

    public static CategoryResponse convertToCategoryResponse(Category category){
        return  category == null
                ? null
                : CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .image(category.getImage())
                .parentId(getParentId(category))
                .isRoot(category.getIsRoot())
                .enabled(category.getEnabled())
                .children(getChildrenList(category))
                .build();
    }

    public static Category convertToCategory(CategoryRequest request){
        return  Category.builder()
                .name(request.getName())
                .image(request.getImage())
                .isRoot(request.getIsRoot())
                .enabled(request.getEnabled())
                .build();
    }

    private static Long getParentId(Category category){
        return  category.getParent() == null ? null : category.getParent().getId();
    }

    private static List<CategoryResponse> getChildrenList(Category category){
        return  category.getChildren() == null
                ? List.of()
                : category.getChildren()
                .stream()
                .map(CategoryConverter::convertToCategoryResponse)
                .toList();
    }
}
