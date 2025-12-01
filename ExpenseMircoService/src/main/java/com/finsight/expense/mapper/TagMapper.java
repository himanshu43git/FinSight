package com.finsight.expense.mapper;

import com.finsight.expense.dto.TagDto;
import com.finsight.expense.model.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
    Tag toEntity(TagDto dto);
}
