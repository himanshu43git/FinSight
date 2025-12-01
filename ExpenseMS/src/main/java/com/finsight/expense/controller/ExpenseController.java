package com.finsight.expense.controller;

import com.finsight.expense.exception.ExpenseAPIException;
import com.finsight.expense.io.request.ExpenseRequest;
import com.finsight.expense.io.response.ExpenseResponse;
import com.finsight.expense.service.ExpenseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    @PostMapping("/addExpense")
    public ExpenseResponse newExpense(@RequestBody ExpenseRequest request){
        return expenseService.addExpense(request)
                .orElseThrow(() -> new ExpenseAPIException("Some Error Occurred !!!"));
    }


}
