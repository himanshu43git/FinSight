package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "expenses")
public class Expense {

    @Id
    @Column(name = "expense_id", nullable = false, updatable = false, length = 36)
    private String expenseId;

    private String userId;

    private String description;

    private double amount;

    // Use Instant for precise timezone-neutral timestamp; maps to TIMESTAMP column
    private Instant date;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "expense_categories", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<Category> category = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "expense_custom_categories", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "custom_category")
    private Set<String> customCategories = new HashSet<>();

    private boolean status;

    @Column(updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}
