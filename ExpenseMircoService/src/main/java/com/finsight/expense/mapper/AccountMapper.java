package com.finsight.expense.mapper;

import com.finsight.expense.dto.AccountDto;
import com.finsight.expense.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto toDto(Account account);
    Account toEntity(AccountDto dto);
}
