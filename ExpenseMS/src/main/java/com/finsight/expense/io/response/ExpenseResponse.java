package com.finsight.expense.io.response;

import com.finsight.expense.model.Category;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class ExpenseResponse {
    private String expenseId;
    private String userId;
    private String description;
    private double amount;
    private Instant date;                // changed to Instant
    private Set<Category> category;
    private Set<String> customCategories;
    private boolean status;
    private Instant createdAt;           // changed to Instant
}
