package ru.yandex.practicum.category.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    @Transactional
    CategoryDto addCategory(NewCategoryDto dto);

    @Transactional
    void deleteCategory(Long catId);

    @Transactional
    CategoryDto updateCategory(NewCategoryDto dto, Long catId);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);
}
