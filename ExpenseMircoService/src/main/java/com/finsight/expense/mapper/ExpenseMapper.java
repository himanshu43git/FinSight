package com.finsight.expense.mapper;

import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import com.finsight.expense.model.Expense;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseMapper {

    @Mapping(target = "tagIds", ignore = true) // handled separately
    Expense toEntity(ExpenseRequest req);

    @Mapping(target = "tagIds", ignore = true)
    ExpenseResponse toDto(Expense expense);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExpenseFromRequest(ExpenseRequest req, @MappingTarget Expense entity);
}
