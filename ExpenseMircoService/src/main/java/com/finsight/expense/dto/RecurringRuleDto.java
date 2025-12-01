package com.finsight.expense.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringRuleDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String frequency;
    private Instant nextRun;
    private String template;
    private Boolean enabled;
}
