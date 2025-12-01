package com.finsight.expense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private UUID id;
    private UUID userId;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private LocalDate date;
    private String merchant;
    private String merchantNormalized;
    private String notes;
    private UUID categoryId;
    private UUID suggestedCategoryId;
    private Set<UUID> tagIds;
    private UUID receiptId;
    private UUID recurringRuleId;
    private boolean isRecurringInstance;
    private boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;
}
