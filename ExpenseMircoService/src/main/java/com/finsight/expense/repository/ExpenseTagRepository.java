package com.finsight.expense.repository;

import com.finsight.expense.model.ExpenseTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseTagRepository extends JpaRepository<ExpenseTag, ExpenseTag.ExpenseTagId> {
    List<ExpenseTag> findByExpenseId(UUID expenseId);
    void deleteByExpenseId(UUID expenseId);
}
