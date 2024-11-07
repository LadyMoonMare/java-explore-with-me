package ru.yandex.practicum.category.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public static Category fromNewCategoryToCategory(NewCategoryDto dto) {
        return new Category(dto.getName());
    }

    public static CategoryDto fromCategoryToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
