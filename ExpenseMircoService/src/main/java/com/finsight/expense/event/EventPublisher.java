package com.finsight.expense.event;

public interface EventPublisher {
    /**
     * Publish an event to the event bus.
     * @param topic logical topic name, e.g., "expenses.created"
     * @param payload object (will be serialized by implementation)
     */
    void publish(String topic, Object payload);
}
