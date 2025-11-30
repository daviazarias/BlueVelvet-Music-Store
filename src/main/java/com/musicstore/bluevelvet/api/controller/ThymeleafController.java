package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller@RequiredArgsConstructor
public class ThymeleafController {

    private final CategoryService service;
    private final Integer defaultDashboardPageSize = 10;
    private final Integer defaultListPageSize = 5;

    @GetMapping("/")
    public String getHomePage(){
        return "index";
    }

    @GetMapping("/create-category")
    public String createCategory(Model model){
        List<CategoryResponse> responseList = service.findAllRoots();
        model.addAttribute("categories", responseList);
        model.addAttribute("category", new CategoryRequest());
        return "create-category";
    }

    @PostMapping("/category")
    public String createCategoryForm(@ModelAttribute CategoryRequest request){

        request.setIsRoot(request.getParentId() != null);

        CategoryResponse response = service.createCategory(request);
        return "redirect:/categories/%d".formatted(response.getId());
    }

    @GetMapping("/dashboard")
    public String categoriesDashboard(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            Model model
    ) {
        Page<CategoryResponse> responsePage = service.findAllRoots(
                PageRequest.of(page, defaultDashboardPageSize, Sort.by(sort)));
        model.addAttribute("categories", responsePage);
        return "dashboard";
    }

    @GetMapping("/list")
    public String categoriesList(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            Model model
    ) {
        Page<CategoryResponse> responsePage = service.findAllRootsWithOrderedChildren(
                PageRequest.of(page, defaultListPageSize, Sort.by(sort))
        );

        model.addAttribute("categories", responsePage);
        model.addAttribute("sort", sort);
        return "list";
    }
}
