package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recurring_rules", indexes = {
        @Index(name = "idx_recurring_user", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringRule {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurrenceFrequency frequency;

    /**
     * Next scheduled run time (UTC)
     */
    @Column(name = "next_run")
    private Instant nextRun;

    /**
     * Template JSON (amount, currency, merchant, categoryId, accountId, tags, notes)
     */
    @Column(columnDefinition = "jsonb")
    private String template;

    @Column(nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
