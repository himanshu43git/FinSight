package com.finsight.expense.service;

import com.finsight.expense.io.request.ExpenseRequest;
import com.finsight.expense.io.response.ExpenseResponse;

import java.util.Optional;

public interface ExpenseService {

    Optional<ExpenseResponse> addExpense(ExpenseRequest request);

}
