package com.finsight.expense.service.impl;

import com.finsight.expense.io.request.ExpenseRequest;
import com.finsight.expense.io.response.ExpenseResponse;
import com.finsight.expense.model.Category;
import com.finsight.expense.model.Expense;
import com.finsight.expense.repository.ExpenseRepository;
import com.finsight.expense.service.ExpenseService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

        ExpenseResponse response = mapToResponse(expense);

        return Optional.of(response);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
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
    }

    @Override
    public Optional<ExpenseResponse> updateExpense(String expenseId, ExpenseRequest request) {

        if (expenseId == null || expenseId.isBlank()) {
            throw new IllegalArgumentException("Expense ID cannot be null or empty");
        }

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NoSuchElementException("Expense not found with ID: " + expenseId));

        // Update description if provided
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            expense.setDescription(request.getDescription().trim());
        }

        // Update amount if provided and > 0
        if (request.getAmount() > 0) {
            expense.setAmount(request.getAmount());
        }

        // Update categories only if client provided a category set (we accept Set<String> in Request)
        // If the request's category is null => keep existing categories as-is
        Set<String> incoming = request.getCategory() == null ? null : request.getCategory();

        if (incoming != null) {
            Set<Category> matchedEnums = new HashSet<>();
            Set<String> customCategories = new HashSet<>();

            for (String raw : incoming) {
                if (raw == null || raw.isBlank()) continue;
                Category.fromString(raw).ifPresentOrElse(
                        matchedEnums::add,
                        () -> customCategories.add(raw.trim().toUpperCase(Locale.ROOT))
                );
            }

            // If nothing matched, fallback to MISCELLANEOUS
            if (matchedEnums.isEmpty() && customCategories.isEmpty()) {
                matchedEnums.add(Category.MISCELLANEOUS);
            }

            // Replace entity categories / customCategories with new ones
            expense.setCategory(matchedEnums);

            // ensure customCategories field exists on Expense entity (Set<String>)
            expense.setCustomCategories(customCategories);
        }

        // Persist changes
        expense = expenseRepository.save(expense);

        // Build response
        ExpenseResponse response = mapToResponse(expense);

        return Optional.of(response);
    }


    @Override
    public Optional<ExpenseResponse> getExpenseById(String expenseId) {
        if (expenseId == null || expenseId.isBlank()) {
            throw new IllegalArgumentException("Expense ID cannot be null or empty");
        }

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NoSuchElementException("Expense not found with ID: " + expenseId));

        ExpenseResponse response = mapToResponse(expense);

        return Optional.of(response);
    }


    @Override
    public Page<ExpenseResponse> getAllExpenses(Optional<Date> start, Optional<Date> end, Pageable pageable) {
        Specification<Expense> spec = buildDateRangeSpec(start, end);

        Page<Expense> expenses = expenseRepository.findAll(spec, pageable);

        // Map Page<Expense> -> Page<ExpenseResponse>
        return expenses.map(this::mapToResponse);
    }

    @Override
    public boolean deleteExpense(String expenseId) {

        if (expenseId == null || expenseId.isBlank()) {
            throw new IllegalArgumentException("Expense ID cannot be null or empty");
        }

        boolean exists = expenseRepository.existsById(expenseId);
        if (!exists) {
            return false; // Expense not found
        }

        expenseRepository.deleteById(expenseId);
        return true; // Deletion successful

    }


    private Specification<Expense> buildDateRangeSpec(Optional<Date> start, Optional<Date> end) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // if both present, use between (inclusive)
            if (start.isPresent() && end.isPresent()) {
                predicates.add(cb.between(root.get("date"), start.get(), end.get()));
            } else if (start.isPresent()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), start.get()));
            } else if (end.isPresent()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), end.get()));
            }

            // You can add tenant/user filtering here if desired (e.g., by userId)
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}
