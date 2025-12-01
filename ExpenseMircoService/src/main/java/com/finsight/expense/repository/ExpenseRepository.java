package com.finsight.expense.repository;

import com.finsight.expense.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

    Page<Expense> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    Page<Expense> findByUserIdAndDateBetweenAndIsDeletedFalse(UUID userId, LocalDate start, LocalDate end, Pageable pageable);

    List<Expense> findTop100ByUserIdAndIsDeletedFalseOrderByDateDesc(UUID userId);

    @Modifying
    @Query("update Expense e set e.isDeleted=true where e.id = :id and e.userId = :userId")
    int softDeleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
