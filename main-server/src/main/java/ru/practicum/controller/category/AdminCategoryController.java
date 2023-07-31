package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.category.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(newCategoryDto));
    }


    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Integer categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("catId") Integer categoryId,
                                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        return ResponseEntity.ok().body(categoryService.updateCategory(categoryId, newCategoryDto));
    }

}
