package com.finsight.expense.mapper;

import com.finsight.expense.dto.RecurringRuleDto;
import com.finsight.expense.model.RecurringRule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecurringRuleMapper {
    RecurringRuleDto toDto(RecurringRule r);
    RecurringRule toEntity(RecurringRuleDto dto);
}
