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

    /**
     * DEDUPLICATION IMPLEMENTATION
     * <p>
     * This implementation handles deduplication by ensuring that both
     * the deduplication check/record AND the business logic happen in the SAME transaction.
     * <p>
     * Benefits:
     * - If business logic fails, the entire transaction rolls back, including the message ID record
     * - Message will be redelivered and processed again (no data loss)
     * - True duplicates (already successfully processed) are correctly skipped
     * - Atomic: either everything succeeds or everything fails
     * <p>
     * Correct Scenario:
     * 1. Message arrives with ID "abc-123"
     * 2. Transaction starts
     * 3. Check if "abc-123" exists - it doesn't
     * 4. Insert "abc-123" into deduplication table
     * 5. Process business logic (save to processed_operations)
     * 6. If processing fails, ENTIRE transaction rolls back (including message ID)
     * 7. Message "abc-123" is redelivered
     * 8. Check if "abc-123" exists - it doesn't (rollback removed it)
     * 9. Process succeeds this time - both tables committed together
     */
    @Transactional
    public void processEvent(OperationCompleted event) {
        log.info("Received event - MessageId: {}, CorrelationId: {}",
                event.getMessageId(), event.getCorrelationId());

        // Check for duplicate in the SAME transaction
        if (messageIdRepository.existsById(event.getMessageId())) {
            log.warn("DUPLICATE DETECTED - Skipping event with MessageId: {}", event.getMessageId());
            return;
        }

        // Record message ID in the SAME transaction
        ProcessedMessageId processedMsgId = new ProcessedMessageId(event.getMessageId());
        messageIdRepository.save(processedMsgId);
        log.debug("Recorded message ID for deduplication: {}", event.getMessageId());

        // Process business logic in the SAME transaction
        // If this fails, everything rolls back including the message ID above
        processBusinessLogic(event);

        // All operations committed atomically when method completes successfully
    }

    /**
     * Processes the business logic within the caller's transaction.
     * Every 10th event will fail to demonstrate that the message is NOT lost
     * (it will be reprocessed successfully on retry).
     */
    private void processBusinessLogic(OperationCompleted event) {
        int count = eventCounter.incrementAndGet();

        // Simulate failure every 10th event to demonstrate correct retry behavior
        if (count % 10 == 0) {
            log.error("SIMULATED FAILURE for event #{} - MessageId: {} (will be retried correctly)",
                    count, event.getMessageId());
            throw new RuntimeException("Simulated processing failure (every 10th event)");
        }

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
