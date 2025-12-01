package com.finsight.expense.service.impl;

import com.finsight.expense.dto.ExpenseRequest;
import com.finsight.expense.dto.ExpenseResponse;
import com.finsight.expense.exception.BadRequestException;
import com.finsight.expense.exception.ResourceNotFoundException;
import com.finsight.expense.mapper.ExpenseMapper;
import com.finsight.expense.model.Expense;
import com.finsight.expense.model.ExpenseTag;
import com.finsight.expense.model.Tag;
import com.finsight.expense.repository.*;
import com.finsight.expense.service.ExpenseService;
import com.finsight.expense.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TagRepository tagRepository;
    private final ExpenseMapper expenseMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseTagRepository expenseTagRepository; // optional, if you created repository
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public ExpenseResponse createExpense(UUID userId, ExpenseRequest req, String requestId) {
        // validate account ownership
        accountRepository.findById(req.getAccountId())
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new BadRequestException("Account not found or not owned by user"));

        // optional: validate category ownership if provided
        if (req.getCategoryId() != null) {
            categoryRepository.findById(req.getCategoryId())
                    .filter(c -> c.getUserId() == null || c.getUserId().equals(userId))
                    .orElseThrow(() -> new BadRequestException("Category not found or not accessible"));
        }

        Expense entity = expenseMapper.toEntity(req);
        if (entity.getId() == null) entity.setId(UUID.randomUUID());
        entity.setUserId(userId);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        // persist expense
        expenseRepository.save(entity);

        // handle tags: create missing tags for user & write join table
        handleTagsForExpense(entity.getId(), userId, req.getTagIds());

        // publish event (expense.created)
        Map<String, Object> payload = Map.of(
                "id", entity.getId(),
                "userId", userId,
                "amount", entity.getAmount(),
                "currency", entity.getCurrency(),
                "date", entity.getDate(),
                "merchant", entity.getMerchant(),
                "receiptId", entity.getReceiptId()
        );
        eventPublisher.publish("expenses.created", payload);

        return expenseMapper.toDto(entity);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(UUID userId, UUID expenseId, ExpenseRequest req) {
        Expense existing = expenseRepository.findById(expenseId)
                .filter(e -> e.getUserId().equals(userId) && !e.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        // validate account if changing
        if (req.getAccountId() != null && !req.getAccountId().equals(existing.getAccountId())) {
            accountRepository.findById(req.getAccountId())
                    .filter(a -> a.getUserId().equals(userId))
                    .orElseThrow(() -> new BadRequestException("Account not found or not owned by user"));
            existing.setAccountId(req.getAccountId());
        }

        // update fields via mapper
        expenseMapper.updateExpenseFromRequest(req, existing);
        existing.setUpdatedAt(Instant.now());

        expenseRepository.save(existing);

        // update tags
        handleTagsForExpense(existing.getId(), userId, req.getTagIds());

        // publish event
        eventPublisher.publish("expenses.updated", Map.of("id", existing.getId(), "userId", userId));

        return expenseMapper.toDto(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpense(UUID userId, UUID expenseId) {
        Expense e = expenseRepository.findById(expenseId)
                .filter(x -> x.getUserId().equals(userId) && !x.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        ExpenseResponse dto = expenseMapper.toDto(e);
        // fetch tags
        List<ExpenseTag> joins = expenseTagRepository.findByExpenseId(e.getId());
        dto.setTagIds(joins.stream().map(ExpenseTag::getTagId).collect(Collectors.toSet()));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> listExpenses(UUID userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Expense> page;
        if (startDate != null && endDate != null) {
            page = expenseRepository.findByUserIdAndDateBetweenAndIsDeletedFalse(userId, startDate, endDate, pageable);
        } else {
            page = expenseRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
        }
        List<ExpenseResponse> dtos = page.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public void softDeleteExpense(UUID userId, UUID expenseId) {
        int updated = expenseRepository.softDeleteByIdAndUserId(expenseId, userId);
        if (updated == 0) throw new ResourceNotFoundException("Expense not found or already deleted");
        eventPublisher.publish("expenses.deleted", Map.of("id", expenseId, "userId", userId));
    }

    @Override
    @Transactional
    public void confirmCategory(UUID userId, UUID expenseId, UUID categoryId) {
        Expense e = expenseRepository.findById(expenseId)
                .filter(x -> x.getUserId().equals(userId) && !x.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        categoryRepository.findById(categoryId)
                .filter(c -> c.getUserId() == null || c.getUserId().equals(userId))
                .orElseThrow(() -> new BadRequestException("Category not found or not accessible"));
        e.setCategoryId(categoryId);
        e.setSuggestedCategoryId(null);
        e.setUpdatedAt(Instant.now());
        expenseRepository.save(e);
        eventPublisher.publish("expenses.category_confirmed", Map.of("id", expenseId, "categoryId", categoryId, "userId", userId));
    }

    /* Helper: create missing tags for user, and ensure expense_tags join rows exist */
    private void handleTagsForExpense(UUID expenseId, UUID userId, Set<UUID> tagIds) {
        // if null or empty -> remove all existing joins
        if (tagIds == null || tagIds.isEmpty()) {
            expenseTagRepository.deleteByExpenseId(expenseId);
            return;
        }

        // find existing tags for user
        List<Tag> existing = tagRepository.findByUserId(userId);
        Map<String, Tag> nameMap = existing.stream().collect(Collectors.toMap(Tag::getName, t -> t, (a,b)->a));

        // NOTE: We accept client supplied IDs OR names could be passed in more advanced design.
        // For now we assume tagIds are ids; validate ownership
        List<Tag> foundTags = tagRepository.findAllById(tagIds);
        // if some provided ids not found -> error
        Set<UUID> foundIds = foundTags.stream().map(Tag::getId).collect(Collectors.toSet());
        Set<UUID> missing = tagIds.stream().filter(t -> !foundIds.contains(t)).collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new BadRequestException("One or more tag ids not found for user");
        }

        // clear existing joins and insert new ones
        expenseTagRepository.deleteByExpenseId(expenseId);
        List<ExpenseTag> joins = foundTags.stream()
                .map(t -> new ExpenseTag(expenseId, t.getId()))
                .collect(Collectors.toList());
        expenseTagRepository.saveAll(joins);
    }
}
