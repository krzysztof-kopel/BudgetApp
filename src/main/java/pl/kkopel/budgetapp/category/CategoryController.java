package pl.kkopel.budgetapp.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Endpoint for querying categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository repository) {
        this.categoryRepository = repository;
    }

    @GetMapping
    @Operation(summary = "Get categories", description = "Provides categories predefined in the app")
    ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = (List<Category>) this.categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }
}
