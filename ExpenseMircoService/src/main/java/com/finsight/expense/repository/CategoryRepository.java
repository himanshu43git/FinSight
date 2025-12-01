package com.finsight.expense.repository;

import com.finsight.expense.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserIdOrUserIdIsNull(UUID userId); // global + user
}
