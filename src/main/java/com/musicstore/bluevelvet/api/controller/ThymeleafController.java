package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller@RequiredArgsConstructor
public class ThymeleafController {

    private final CategoryService service;

    @GetMapping("/")
    public String getHomePage(){
        return "index";
    }

    @GetMapping("/category")
    public String createCategory(Model model){
        List<CategoryResponse> responseList = service.findAll();
        model.addAttribute("categories", responseList);
        model.addAttribute("category", new CategoryRequest());
        return "create-category";
    }

    @PostMapping("/category")
    public String createCategoryForm(@ModelAttribute CategoryRequest request){
        CategoryResponse response = service.createCategory(request);
        return "redirect:/categories/%d".formatted(response.getId());
    }
}
