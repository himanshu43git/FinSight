package com.finsight.expense.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_expenses_user_date", columnList = "user_id, date"),
        @Index(name = "idx_expenses_user_category_date", columnList = "user_id, category_id, date"),
        @Index(name = "idx_expenses_merchant_normalized", columnList = "merchant_normalized")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 3)
    @Column(length = 3, nullable = false)
    private String currency; // ISO 4217

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    private String merchant;

    @Column(name = "merchant_normalized")
    private String merchantNormalized;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "category_id", columnDefinition = "uuid")
    private UUID categoryId;          // confirmed

    @Column(name = "suggested_category_id", columnDefinition = "uuid")
    private UUID suggestedCategoryId; // suggested by AI

    @Column(name = "receipt_id", columnDefinition = "uuid")
    private UUID receiptId;

    @Column(name = "recurring_rule_id", columnDefinition = "uuid")
    private UUID recurringRuleId;

    @Column(name = "is_recurring_instance", nullable = false)
    private boolean isRecurringInstance = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    // tags are stored via expense_tags join table - keep convenience transient set for DTOs
    @Transient
    private Set<UUID> tagIds;
}
