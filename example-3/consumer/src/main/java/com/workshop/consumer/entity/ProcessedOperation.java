package com.workshop.consumer.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_operations", schema = "consumer_schema")
public class ProcessedOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedOperation() {
    }

    public ProcessedOperation(String messageId, String correlationId, String info,
                             String status, Instant eventTimestamp) {
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.info = info;
        this.status = status;
        this.eventTimestamp = eventTimestamp;
        this.processedAt = Instant.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
