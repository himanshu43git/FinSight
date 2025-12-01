package com.finsight.expense.repository;

import com.finsight.expense.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    List<Tag> findByUserId(UUID userId);
}
