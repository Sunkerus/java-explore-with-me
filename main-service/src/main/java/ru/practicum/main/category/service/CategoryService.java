package ru.practicum.main.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategoryAsAdmin(NewCategoryDto newCategoryDto);

    void deleteCategoryAsAdmin(Long catId);

    CategoryDto patchCategoryAsAdmin(CategoryDto categoryDto, Long catId);

    Category getCategoryById(Long catId);

    List<CategoryDto> getCategoryAsPublic(Pageable page);

    CategoryDto getCategoryByIdAsPublic(Long catId);

}
