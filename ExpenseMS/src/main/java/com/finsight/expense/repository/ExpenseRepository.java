package com.finsight.expense.repository;

import com.finsight.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {

    Optional<Expense> findByExpenseId(String expenseId);

}
