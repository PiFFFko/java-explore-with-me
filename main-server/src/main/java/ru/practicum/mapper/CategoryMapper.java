package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.category.dto.NewCategoryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto entityToDto(Category category);

    Category newCategoryDtoToEntity(NewCategoryDto newCategoryDto);

    List<CategoryDto> listEntitiesToListDtos(List<Category> categories);
}
