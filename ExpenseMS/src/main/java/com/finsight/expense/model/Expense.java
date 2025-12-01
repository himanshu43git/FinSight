package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
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
    private String expenseId; // service sets UUID string before save

    private String userId;

    private String description;

    private double amount;

    private Date date;

    // Persist enum categories as strings in table expense_categories (expense_id -> category)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_categories", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<Category> category = new HashSet<>();

    // Persist unknown/custom categories as strings
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_custom_categories", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "custom_category")
    private Set<String> customCategories = new HashSet<>();

    private boolean status;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
