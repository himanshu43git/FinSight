package com.finsight.expense.io.request;

import lombok.Data;

import java.util.Set;

@Data
public class ExpenseRequest {
    private String userId;
    private String description;
    private double amount;

    // accept arbitrary strings from client (case-insensitive)
    private Set<String> category;
}
