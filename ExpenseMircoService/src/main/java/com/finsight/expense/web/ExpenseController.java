package com.finsight.expense.web;

import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import com.finsight.expense.exception.BadRequestException;
import com.finsight.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Validated
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Validated ExpenseRequest req,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        UUID userId = UUID.fromString(jwt.getSubject());
        ExpenseResponse created = expenseService.createExpense(userId, req, requestId);
        URI loc = URI.create("/api/v1/expenses/" + created.getId());
        return ResponseEntity.created(loc).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @RequestBody @Validated ExpenseRequest req) {

        UUID userId = UUID.fromString(jwt.getSubject());
        ExpenseResponse resp = expenseService.updateExpense(userId, id, req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        ExpenseResponse resp = expenseService.getExpense(userId, id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> listExpenses(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "sort", defaultValue = "date,desc") String sort) {

        UUID userId = UUID.fromString(jwt.getSubject());
        LocalDate startDate = (start == null ? null : LocalDate.parse(start));
        LocalDate endDate = (end == null ? null : LocalDate.parse(end));

        String[] sortParts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(sortParts.length > 1 ? sortParts[1] : "DESC"), sortParts[0]);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, size), s);

        Page<ExpenseResponse> results = expenseService.listExpenses(userId, startDate, endDate, pageable);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        expenseService.softDeleteExpense(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm-category")
    public ResponseEntity<Void> confirmCategory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @RequestBody ConfirmCategoryRequest body) {

        if (body.getCategoryId() == null) throw new BadRequestException("categoryId is required");
        UUID userId = UUID.fromString(jwt.getSubject());
        expenseService.confirmCategory(userId, id, body.getCategoryId());
        return ResponseEntity.ok().build();
    }

    // inner DTO for confirm category
    public static class ConfirmCategoryRequest {
        private UUID categoryId;
        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    }
}
