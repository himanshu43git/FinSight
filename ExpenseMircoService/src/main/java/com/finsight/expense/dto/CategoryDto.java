package com.finsight.expense.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private UUID id;
    private UUID userId;
    private String name;
    private UUID parentId;
}
