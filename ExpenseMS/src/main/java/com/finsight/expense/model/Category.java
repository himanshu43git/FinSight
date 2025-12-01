package com.finsight.expense.model;

import java.util.Locale;
import java.util.Optional;

public enum Category {
    FOOD,
    TRANSPORTATION,
    UTILITIES,
    ENTERTAINMENT,
    HEALTHCARE,
    EDUCATION,
    GROCERIES,
    RENT,
    TRAVEL,
    MISCELLANEOUS;

    /**
     * Try to convert a string to a Category (case-insensitive).
     * Returns Optional.empty() if no enum constant matches.
     */
    public static Optional<Category> fromString(String raw) {
        if (raw == null) return Optional.empty();
        String key = raw.trim().replaceAll("\\s+", "_").toUpperCase(Locale.ROOT);
        try {
            return Optional.of(Category.valueOf(key));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
