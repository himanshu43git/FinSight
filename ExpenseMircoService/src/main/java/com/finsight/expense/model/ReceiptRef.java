package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight reference to Receipt Service metadata.
 * The actual receipt binary/object lives in Receipt Service / S3.
 */
@Entity
@Table(name = "receipts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRef {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Id used by Receipt Service (if different) — optional pointer
     */
    @Column(name = "receipt_service_id", columnDefinition = "uuid")
    private UUID receiptServiceId;

    /**
     * Small JSON metadata (ocr status, extracted fields)
     */
    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
