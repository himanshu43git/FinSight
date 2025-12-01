package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Normalized merchant / alias table to group similar merchant names.
 * AI service can insert/update aliases and Expense can reference normalized merchant or alias id.
 */
@Entity
@Table(name = "merchant_aliases", indexes = {
        @Index(name = "idx_merchant_alias_normalized", columnList = "normalized_name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAlias {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * canonical normalized merchant name
     */
    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    /**
     * example raw name matched (for human review)
     */
    @Column(name = "example_raw")
    private String exampleRaw;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
