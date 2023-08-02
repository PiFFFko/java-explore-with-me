package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.category.dto.NewCategoryDto;
import ru.practicum.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = categoryMapper.newCategoryDtoToEntity(newCategoryDto);
        return categoryMapper.entityToDto(categoryRepository.save(newCategory));
    }

    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        categoryRepository.deleteById(categoryId);
    }

    public CategoryDto updateCategory(Integer categoryId, NewCategoryDto newCategoryDto) {
        Category categoryToUpdate = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        Category newCategory = categoryMapper.newCategoryDtoToEntity(newCategoryDto);
        newCategory.setId(categoryId);
        categoryRepository.save(newCategory);
        return categoryMapper.entityToDto(newCategory);
    }

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        List<CategoryDto> result = new ArrayList<>();
        int page = from > 0 ? from / size : 0;
        Pageable categoryPageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(categoryPageable);
        result.addAll(categoryMapper.listEntitiesToListDtos(categoryPage.getContent()));
        return result;
    }

    public CategoryDto getCategoryById(Integer categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return categoryMapper.entityToDto(category);
    }


}
