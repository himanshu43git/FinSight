package com.finsight.expense.service;

import com.finsight.expense.io.request.ExpenseRequest;
import com.finsight.expense.io.response.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;

public interface ExpenseService {

    Optional<ExpenseResponse> addExpense(ExpenseRequest request);

    Optional<ExpenseResponse> updateExpense(String expenseId, ExpenseRequest request);

    Optional<ExpenseResponse> getExpenseById(String expenseId);

    // in ExpenseService.java
    Page<ExpenseResponse> getAllExpenses(Optional<Date> start, Optional<Date> end, Pageable pageable);

    boolean deleteExpense(String expenseId);

}
