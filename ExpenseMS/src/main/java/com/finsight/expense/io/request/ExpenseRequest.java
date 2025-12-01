package com.finsight.expense.io.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class ExpenseRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @Size(max = 1000, message = "description max length is 1000")
    private String description;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be > 0")
    private Double amount;

    private Set<String> category; // client-provided strings (case-insensitive)
}
