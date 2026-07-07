package com.workshop.consumer.service;

import com.workshop.consumer.entity.ProcessedOperation;
import com.workshop.consumer.repository.ProcessedOperationRepository;
import com.workshop.event.OperationCompletedProto.OperationCompleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EventProcessor {
    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    private final ProcessedOperationRepository repository;

    public EventProcessor(ProcessedOperationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processEvent(OperationCompleted event) {
        log.info("Processing event - MessageId: {}, CorrelationId: {}, Info: {}, Status: {}",
                 event.getMessageId(),
                 event.getCorrelationId(),
                 event.getInfo(),
                 event.getStatus());

        ProcessedOperation operation = new ProcessedOperation(
                event.getMessageId(),
                event.getCorrelationId(),
                event.getInfo(),
                event.getStatus(),
                Instant.ofEpochMilli(event.getTimestamp())
        );

        repository.save(operation);
        log.info("Saved processed operation - MessageId: {}", event.getMessageId());
    }
}
