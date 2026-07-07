package com.workshop.producer.service;

import com.workshop.event.OperationCompletedProto.OperationCompleted;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class EventProducer {
    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final String topicName;

    public EventProducer(KafkaTemplate<String, byte[]> kafkaTemplate,
                         @Value("${kafka.topic.operation-completed}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

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

        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                topicName,
                null,
                messageId,
                event.toByteArray()
        );

        // Add headers
        record.headers().add(new RecordHeader("messageId", messageId.getBytes()));
        record.headers().add(new RecordHeader("correlationId", correlationId.getBytes()));

        // there are some problem with this code... let's discuss!

        kafkaTemplate.send(record);
        log.info("Published event to Kafka - MessageId: {}, CorrelationId: {}",
                 messageId, correlationId);
    }
}
