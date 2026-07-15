package com.bloquinho.category.api;

import com.bloquinho.category.application.ListPublicCategoriesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/categories")
public class PublicCategoryController {
    private final ListPublicCategoriesUseCase listPublicCategories;

    public PublicCategoryController(ListPublicCategoriesUseCase listPublicCategories) {
        this.listPublicCategories = listPublicCategories;
    }

    @Operation(summary = "List active professional categories")
    @ApiResponse(responseCode = "200", description = "Active categories ordered by name")
    @GetMapping
    public Map<String, List<PublicCategoryResponse>> list() {
        var categories = listPublicCategories.execute().stream()
            .map(PublicCategoryResponse::from)
            .toList();
        return Map.of("data", categories);
    }
}
