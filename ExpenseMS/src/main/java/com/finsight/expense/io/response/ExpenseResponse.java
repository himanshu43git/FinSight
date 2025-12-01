package com.finsight.expense.io.response;

import com.finsight.expense.model.Category;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class ExpenseResponse {
    private String expenseId;
    private String userId;
    private String description;
    private double amount;
    private Date date;
    private Set<Category> category;
    private Set<String> customCategories; // unknown categories preserved here
    private boolean status;
    private LocalDateTime createdAt;
}
