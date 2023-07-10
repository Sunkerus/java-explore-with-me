package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exeption.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryRepository getCategoryRepository() {
        return this.categoryRepository;
    }


    @Transactional
    @Override
    public CategoryDto createCategoryAsAdmin(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Transactional
    @Override
    public void deleteCategoryAsAdmin(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id: %d cannot found", catId)));
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto patchCategoryAsAdmin(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Category with this id: %d cannot found", catId));
        });
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategoryAsPublic(Pageable page) {
        return categoryRepository.findAll(page).map(CategoryMapper::toDto).getContent();
    }

    @Override
    public CategoryDto getCategoryByIdAsPublic(Long catId) {
        return CategoryMapper.toDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id: %d cannot found", catId))));
    }
}
