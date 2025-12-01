package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_user", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Null indicates a global category (shared builtin)
     */
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(name = "parent_id", columnDefinition = "uuid")
    private UUID parentId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
