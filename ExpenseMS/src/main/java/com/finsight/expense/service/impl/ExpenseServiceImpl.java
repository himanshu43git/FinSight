package com.finsight.expense.service.impl;

import com.finsight.expense.io.request.ExpenseRequest;
import com.finsight.expense.io.response.ExpenseResponse;
import com.finsight.expense.model.Category;
import com.finsight.expense.model.Expense;
import com.finsight.expense.repository.ExpenseRepository;
import com.finsight.expense.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Optional<ExpenseResponse> addExpense(ExpenseRequest request) {

        if (request == null) return Optional.empty();

        if (request.getAmount() <= 0) {
            return Optional.empty();
        }

        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        String desc = (request.getDescription() == null || request.getDescription().isBlank())
                ? "No description provided"
                : request.getDescription().trim();

        // incoming strings (defensive)
        Set<String> incoming = request.getCategory() == null ? Set.of() : request.getCategory();

        Set<Category> matchedEnums = new HashSet<>();
        Set<String> customCategories = new HashSet<>();

        for (String raw : incoming) {
            if (raw == null || raw.isBlank()) continue;
            // try to map to enum
            Category.fromString(raw).ifPresentOrElse(
                    matchedEnums::add,
                    () -> customCategories.add(raw.trim().toUpperCase(Locale.ROOT))
            );
        }

        // fallback if both are empty
        if (matchedEnums.isEmpty() && customCategories.isEmpty()) {
            matchedEnums.add(Category.MISCELLANEOUS);
        }

        Expense expense = Expense.builder()
                .expenseId(UUID.randomUUID().toString()) // your approach: set ID here
                .userId(request.getUserId())
                .description(desc)
                .amount(request.getAmount())
                .date(new Date())
                .category(matchedEnums)
                .customCategories(customCategories)
                .status(true)
                .build();

        // persist
        expense = expenseRepository.save(expense);

        ExpenseResponse response = ExpenseResponse.builder()
                .expenseId(expense.getExpenseId())
                .userId(expense.getUserId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .category(expense.getCategory())
                .customCategories(expense.getCustomCategories())
                .status(expense.isStatus())
                .createdAt(expense.getCreatedAt())
                .build();

        return Optional.of(response);
    }


}
