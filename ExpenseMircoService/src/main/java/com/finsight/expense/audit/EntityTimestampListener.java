package com.finsight.expense.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;
import java.lang.reflect.Field;

public class EntityTimestampListener {

    @PrePersist
    public void setCreatedAt(Object entity) {
        try {
            Field created = entity.getClass().getDeclaredField("createdAt");
            created.setAccessible(true);
            if (created.get(entity) == null) {
                created.set(entity, Instant.now());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            // log if you want
        }
    }

    @PreUpdate
    public void setUpdatedAt(Object entity) {
        try {
            Field updated = entity.getClass().getDeclaredField("updatedAt");
            updated.setAccessible(true);
            updated.set(entity, Instant.now());
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            // log if you want
        }
    }
}
