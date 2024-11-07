package ru.yandex.practicum.category.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.service.CategoryServiceImpl;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryServiceImpl categoryService;

    @GetMapping("/{catId}")
    @Validated
    public CategoryDto getCategoryById(@PathVariable @Positive Long catId) {
        log.info("attempt to get category id = {}", catId);
        return categoryService.getCategoryById(catId);
    }

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("attempt to get categories by params: from {}, size {}");
        return categoryService.getCategories(from,size);
    }
}
