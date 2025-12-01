package com.finsight.expense.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {

    private UUID id; // optional client-generated

    @NotNull
    private UUID accountId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 3)
    private String currency;

    @NotNull
    private LocalDate date;

    private String merchant;
    private String notes;

    private UUID categoryId;
    private Set<UUID> tagIds;
    private UUID receiptId;
    private UUID recurringRuleId;
}
