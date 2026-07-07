package com.workshop.consumer.service;

import com.workshop.consumer.entity.ProcessedMessageId;
import com.workshop.consumer.entity.ProcessedOperation;
import com.workshop.consumer.repository.ProcessedMessageIdRepository;
import com.workshop.consumer.repository.ProcessedOperationRepository;
import com.workshop.event.OperationCompletedProto.OperationCompleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EventProcessor {
    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    private final ProcessedOperationRepository operationRepository;
    private final ProcessedMessageIdRepository messageIdRepository;
    private final AtomicInteger eventCounter = new AtomicInteger(0);

    public EventProcessor(ProcessedOperationRepository operationRepository,
                          ProcessedMessageIdRepository messageIdRepository) {
        this.operationRepository = operationRepository;
        this.messageIdRepository = messageIdRepository;
    }

    public void processEvent(OperationCompleted event) {
        log.info("Received event - MessageId: {}, CorrelationId: {}",
                event.getMessageId(), event.getCorrelationId());

        if (checkDuplicate(event.getMessageId())) {
            log.info("DUPLICATE DETECTED - Skipping event with MessageId: {}", event.getMessageId());
            return;
        }

        recordMessageId(event.getMessageId());
        processBusinessLogic(event);
    }

    boolean checkDuplicate(String messageId) {
        return messageIdRepository.existsById(messageId);
    }

    public void recordMessageId(String messageId) {
        ProcessedMessageId processedMsgId = new ProcessedMessageId(messageId);
        messageIdRepository.save(processedMsgId);
        log.info("Recorded message ID for deduplication: {}", messageId);
    }

    public void processBusinessLogic(OperationCompleted event) {

        /*
        int count = eventCounter.incrementAndGet();

        // Simulate failure every 10th event
        if (count % 10 == 0) {
            log.error("SIMULATED FAILURE for event #{} - MessageId: {}", count, event.getMessageId());
            throw new RuntimeException("Simulated processing failure (every 10th event)");
        }
        */

        /*
        log.info("Processing event #{} - MessageId: {}, Info: {}, Status: {}",
                count,
                event.getMessageId(),
                event.getInfo(),
                event.getStatus());
        */
        ProcessedOperation operation = new ProcessedOperation(
                event.getMessageId(),
                event.getCorrelationId(),
                event.getInfo(),
                event.getStatus(),
                Instant.ofEpochMilli(event.getTimestamp())
        );

        operationRepository.save(operation);
        log.info("Saved processed operation - MessageId: {}", event.getMessageId());
    }
}
