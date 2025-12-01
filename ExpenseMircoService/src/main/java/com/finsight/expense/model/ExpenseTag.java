package com.finsight.expense.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "expense_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ExpenseTag.ExpenseTagId.class)
public class ExpenseTag {

    @Id
    @Column(name = "expense_id", columnDefinition = "uuid")
    private UUID expenseId;

    @Id
    @Column(name = "tag_id", columnDefinition = "uuid")
    private UUID tagId;

    @Data
    public static class ExpenseTagId implements Serializable {
        private UUID expenseId;
        private UUID tagId;
    }
}
