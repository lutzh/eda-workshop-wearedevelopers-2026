package com.workshop.producer.service;

import com.workshop.producer.entity.OutboxEvent;
import com.workshop.producer.repository.OutboxEventRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OutboxPoller {
    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final String topicName;

    public OutboxPoller(OutboxEventRepository outboxEventRepository,
                        KafkaTemplate<String, byte[]> kafkaTemplate,
                        @Value("${kafka.topic.operation-completed}") String topicName) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Scheduled(fixedRate = 3000)  // Poll every 3 seconds
    @Transactional
    public void pollAndPublish() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Found {} pending events in outbox", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                publishEvent(event);

                // Publish each event TWICE to demonstrate need for deduplication
                publishEvent(event);

                // Mark as published after successful publishing
                event.setStatus("PUBLISHED");
                event.setPublishedAt(Instant.now());
                outboxEventRepository.save(event);

                log.info("Published event (twice) and marked as PUBLISHED - MessageId: {}", event.getMessageId());
            } catch (Exception e) {
                log.error("Failed to publish event - MessageId: {}. Will retry.", event.getMessageId(), e);
                // Don't mark as published - it will be retried on next poll
                // This ensures ordering: we won't process the next event until this one succeeds
                break;
            }
        }
    }

    private void publishEvent(OutboxEvent event) throws ExecutionException, InterruptedException {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                topicName,
                null,
                event.getMessageId(),
                event.getEventData()
        );

        // Add headers
        record.headers().add(new RecordHeader("messageId", event.getMessageId().getBytes()));
        record.headers().add(new RecordHeader("correlationId", event.getCorrelationId().getBytes()));

        // Synchronously wait for send to complete to handle failures. Not great, but simple for demo.
        kafkaTemplate.send(record).get();

        log.debug("Sent message to Kafka - MessageId: {}", event.getMessageId());
    }
}
