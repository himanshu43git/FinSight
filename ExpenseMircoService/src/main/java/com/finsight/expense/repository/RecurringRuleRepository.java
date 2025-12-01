package com.finsight.expense.repository;

import com.finsight.expense.model.RecurringRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RecurringRuleRepository extends JpaRepository<RecurringRule, UUID> {
    List<RecurringRule> findByNextRunBeforeAndEnabled(Instant when, Boolean enabled);
    List<RecurringRule> findByUserId(UUID userId);
}
