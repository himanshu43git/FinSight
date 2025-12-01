package com.finsight.expense.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoopEventPublisher implements EventPublisher {

    @Override
    public void publish(String topic, Object payload) {
        // No-op for local/dev; replace with Kafka/Spring Cloud Stream implementation
        log.debug("EventPublisher(Noop) publish topic={} payload={}", topic, payload);
    }
}
