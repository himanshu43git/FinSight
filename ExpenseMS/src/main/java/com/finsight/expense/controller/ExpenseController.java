package com.finsight.expense.controller;

import com.finsight.expense.exception.ExpenseAPIException;
import com.finsight.expense.io.request.ExpenseRequest;
import com.finsight.expense.io.response.ExpenseResponse;
import com.finsight.expense.service.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;

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

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable("id") String expenseId,
                                                         @RequestBody @Validated ExpenseRequest request){

        ExpenseResponse response = expenseService.updateExpense(expenseId, request)
                .orElseThrow(() -> new ExpenseAPIException("Expense Not Found with id: " + expenseId));

        return ResponseEntity.ok(response);

    }


    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable("id") String expenseId){
        ExpenseResponse response = expenseService.getExpenseById(expenseId)
                .orElseThrow(() -> new ExpenseAPIException("Expense Not Found with id: " + expenseId));

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sort", defaultValue = "date,desc") String sort) {

        // Parse dates (accepts ISO date-time or ISO date)
        Optional<Date> startDate;
        Optional<Date> endDate;
        try {
            startDate = parseDateParam(start);
            endDate = parseDateParam(end);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build(); // invalid date format
        }

        // Build Pageable from page/size/sort
        Pageable pageable = PageRequest.of(page, size, parseSortParam(sort));

        // Delegate to service (service method signature shown below)
        Page<ExpenseResponse> expensesPage = expenseService.getAllExpenses(startDate, endDate, pageable);

        return ResponseEntity.ok(expensesPage);
    }

    /**
     * Parse a date/time string to Optional<Date>.
     * Accepts:
     * - ISO date-time: 2025-12-01T10:15:30
     * - ISO date: 2025-12-01 (interpreted as start of day)
     * Returns Optional.empty() if input is null/blank.
     */
    private Optional<Date> parseDateParam(String input) {
        if (input == null || input.isBlank()) return Optional.empty();

        // Try ISO date-time first
        try {
            LocalDateTime ldt = LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME);
            return Optional.of(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
        } catch (DateTimeParseException ignored) {}

        // Try ISO date (treat as start of day)
        LocalDate ld = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        LocalDateTime startOfDay = ld.atStartOfDay();
        return Optional.of(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * Parse a sort string like "date,desc" or "amount,asc" or "date,desc;amount,asc"
     * into a Spring Data Sort object.
     */
    private Sort parseSortParam(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "date");
        }

        // Support multiple sort segments separated by ';' or '|'
        String[] segments = sortParam.split("[;|]");

        Sort combined = Sort.unsorted();
        for (String segment : segments) {
            String[] parts = segment.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = Sort.Direction.DESC;
            if (parts.length > 1) {
                try {
                    direction = Sort.Direction.fromString(parts[1].trim());
                } catch (IllegalArgumentException ignored) {
                    // fallback to DESC
                }
            }
            Sort s = Sort.by(direction, property);
            combined = combined.and(s);
        }

        return combined.isSorted() ? combined : Sort.by(Sort.Direction.DESC, "date");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable("id") String expenseId){
        boolean deleted = expenseService.deleteExpense(expenseId);
        if (!deleted) {
            throw new ExpenseAPIException("Expense Not Found with id: " + expenseId);
        }
        return ResponseEntity.noContent().build();
    }

}
