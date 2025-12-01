package com.finsight.expense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String type;
    private String currency;
    private BigDecimal balance;
}
