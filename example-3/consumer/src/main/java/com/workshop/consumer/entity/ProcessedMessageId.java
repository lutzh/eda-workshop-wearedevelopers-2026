package com.workshop.consumer.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_message_ids", schema = "consumer_schema")
public class ProcessedMessageId {
    @Id
    @Column(name = "message_id")
    private String messageId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedMessageId() {
    }

    public ProcessedMessageId(String messageId) {
        this.messageId = messageId;
        this.processedAt = Instant.now();
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
