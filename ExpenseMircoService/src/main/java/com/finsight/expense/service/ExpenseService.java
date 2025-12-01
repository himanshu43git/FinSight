package com.finsight.expense.service;

import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ExpenseService {

    ExpenseResponse createExpense(UUID userId, ExpenseRequest req, String requestId);

    ExpenseResponse updateExpense(UUID userId, UUID expenseId, ExpenseRequest req);

    ExpenseResponse getExpense(UUID userId, UUID expenseId);

    Page<ExpenseResponse> listExpenses(UUID userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void softDeleteExpense(UUID userId, UUID expenseId);

    void confirmCategory(UUID userId, UUID expenseId, UUID categoryId);
}
