package com.workshop.producer.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events", schema = "producer_schema")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Column(name = "event_data", nullable = false)
    private byte[] eventData;

    @Column(name = "status", nullable = false)
    private String status;  // PENDING, PUBLISHED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    public OutboxEvent() {
    }

    public OutboxEvent(String messageId, String correlationId, byte[] eventData) {
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.eventData = eventData;
        this.status = "PENDING";
        this.createdAt = Instant.now();
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

    public byte[] getEventData() {
        return eventData;
    }

    public void setEventData(byte[] eventData) {
        this.eventData = eventData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
