package com.finsight.expense.mapper;

import com.finsight.expense.dto.CategoryDto;
import com.finsight.expense.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto dto);
}
