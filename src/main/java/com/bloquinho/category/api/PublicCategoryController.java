package com.bloquinho.category.api;

import com.bloquinho.category.application.GetPublicCategoryDetailsUseCase;
import com.bloquinho.category.application.ListPublicCategoriesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/public/categories")
@Validated
public class PublicCategoryController {
    private final ListPublicCategoriesUseCase listPublicCategories;
    private final GetPublicCategoryDetailsUseCase getPublicCategoryDetails;

    public PublicCategoryController(
        ListPublicCategoriesUseCase listPublicCategories,
        GetPublicCategoryDetailsUseCase getPublicCategoryDetails
    ) {
        this.listPublicCategories = listPublicCategories;
        this.getPublicCategoryDetails = getPublicCategoryDetails;
    }

    @Operation(
        summary = "List active professional categories",
        description = "Returns active categories only, ordered by name, with public fields."
    )
    @ApiResponse(responseCode = "200", description = "Active categories ordered by name")
    @ApiResponse(responseCode = "405", description = "HTTP method not supported")
    @GetMapping
    public Map<String, List<PublicCategoryResponse>> list() {
        var categories = listPublicCategories.execute().stream()
            .map(PublicCategoryResponse::from)
            .toList();
        return Map.of("data", categories);
    }

    @Operation(
        summary = "Get an active category and its active professionals",
        description = "Returns an active category and active professionals ordered by name. Missing and inactive categories are indistinguishable."
    )
    @ApiResponse(responseCode = "200", description = "Category details with professionals ordered by name")
    @ApiResponse(responseCode = "400", description = "Malformed category slug")
    @ApiResponse(responseCode = "404", description = "Active category not found")
    @ApiResponse(responseCode = "405", description = "HTTP method not supported")
    @GetMapping("/{slug}")
    public Map<String, PublicCategoryDetailsResponse> details(
        @Parameter(
            description = "Lowercase alphanumeric category slug, optionally separated by hyphens",
            example = "eletrica"
        )
        @PathVariable
        @Pattern(
            regexp = "[a-z0-9]+(?:-[a-z0-9]+)*",
            message = "Slug inválido."
        )
        String slug
    ) {
        return Map.of("data", PublicCategoryDetailsResponse.from(getPublicCategoryDetails.execute(slug)));
    }
}
