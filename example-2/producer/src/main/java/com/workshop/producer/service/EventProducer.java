package com.workshop.producer.service;

import com.workshop.event.OperationCompletedProto.OperationCompleted;
import com.workshop.producer.entity.OutboxEvent;
import com.workshop.producer.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EventProducer {
    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    private final OutboxEventRepository outboxEventRepository;

    public EventProducer(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void produceEvent() {
        String messageId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();

        OperationCompleted event = OperationCompleted.newBuilder()
                .setMessageId(messageId)
                .setCorrelationId(correlationId)
                .setInfo("Placeholder operation info")
                .setStatus("COMPLETED")
                .setTimestamp(Instant.now().toEpochMilli())
                .build();

        OutboxEvent outboxEvent = new OutboxEvent(
                messageId,
                correlationId,
                event.toByteArray()
        );

        outboxEventRepository.save(outboxEvent);
        log.info("Saved event to outbox - MessageId: {}, CorrelationId: {}",
                 messageId, correlationId);
    }
}
