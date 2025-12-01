package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight audit log for changes to expenses (you may also send audit events to ELK).
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user", columnList = "user_id"),
        @Index(name = "idx_audit_entity", columnList = "entity_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "entity_type")
    private String entityType; // e.g., "Expense"

    @Column(name = "entity_id", columnDefinition = "uuid")
    private UUID entityId;

    @Column(name = "action")
    private String action; // CREATE, UPDATE, DELETE

    @Column(name = "before_state", columnDefinition = "jsonb")
    private String beforeState;

    @Column(name = "after_state", columnDefinition = "jsonb")
    private String afterState;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
