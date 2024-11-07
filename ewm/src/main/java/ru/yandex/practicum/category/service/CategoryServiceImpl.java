package ru.yandex.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.dto.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto addCategory(NewCategoryDto dto) {
        try {
            log.info("attempt to add category {} to repo", dto.getName());
            Category category = categoryRepository.save(CategoryMapper.fromNewCategoryToCategory(dto));
            log.info("adding success");
            return CategoryMapper.fromCategoryToDto(category);
        } catch (DataIntegrityViolationException e) {
            log.warn("adding category failure");
            throw new ConflictException("Category " + dto.getName() + "is already exist");
        }
    }

    @Transactional
    public void deleteCategory(Long catId) {
        //ADD 409
        log.info("attempt to delete category id = {} from repo",catId);
        Category category = getCategory(catId);
        categoryRepository.delete(category);
        log.info("deleting success");
    }

    @Transactional
    public CategoryDto updateCategory(NewCategoryDto dto, Long catId) {
        try {
            log.info("attempt to update category id = {}", catId);
            Category category = getCategory(catId);

            category.setName(dto.getName());
            categoryRepository.save(category);
            log.info("updating success");
            return CategoryMapper.fromCategoryToDto(category);
        }  catch (DataIntegrityViolationException e) {
            log.warn("updating category failure");
            throw new ConflictException("Category " + dto.getName() + "is already exist");
        }
    }

    public CategoryDto getCategoryById(Long catId) {
        Category category = getCategory(catId);
        log.info("getting success");
        return CategoryMapper.fromCategoryToDto(category);
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("attempt to get categories from repo");
        return categoryRepository.getAllButLimit(from, size).stream()
                .map(CategoryMapper::fromCategoryToDto)
                .collect(Collectors.toList());
    }

    private Category getCategory(Long catId) {
        log.info("attempt to find category with id = {}", catId);
        return categoryRepository.findById(catId).orElseThrow(() -> {
            log.info("category deleting failure");
            return new NotFoundException("category with id = " + catId +" is not found");
        });
    }
}
