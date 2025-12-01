package com.finsight.expense.repository;

import com.finsight.expense.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String>, JpaSpecificationExecutor<Expense> {

    Optional<Expense> findByExpenseId(String expenseId);

    Page<Expense> findAllByDateBetween(Instant start, Instant end, Pageable pageable);

    Page<Expense> findAllByDateAfter(Instant start, Pageable pageable);

    Page<Expense> findAllByDateBefore(Instant end, Pageable pageable);

}
