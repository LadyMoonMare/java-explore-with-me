package ru.yandex.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.service.CategoryService;

@RestController
@Slf4j
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto category) {
        log.info("attempt to add new category {}", category.getName());
        return categoryService.addCategory(category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Validated
    public void deleteCategory(@PathVariable @Positive Long catId) {
        log.info("attempt to delete category id = {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    @Validated
    public CategoryDto patchCategory(@RequestBody @Valid NewCategoryDto category,
                                     @PathVariable @Positive Long catId) {
        log.info("attempt to update category id = {} with name {}",catId, category.getName());
        return categoryService.updateCategory(category, catId);
    }
}
