# Event-Driven Architecture Workshop

A hands-on workshop demonstrating key patterns in event-driven architecture using Java, Spring Boot, Apache Kafka, and PostgreSQL.

_The repository will be made private again after the workshop, so make sure you don't delete your local clone!_



## Overview

This repository contains 4 progressive examples that demonstrate:
1. **Transactional Outbox Pattern** - Ensuring reliable event publishing
2. **Idempotent Consumer Pattern** - Preventing duplicate message processing
3. **Common Pitfalls** - What can go wrong and how to avoid it

Each example builds on the previous one, showing both incorrect and correct implementations to help you understand the patterns.

## Examples

### [Example 1: No Outbox, No Deduplication](./example-1/)
The simplest approach - direct Kafka publishing without any safety guarantees.

**Demonstrates:**
- Direct event publishing to Kafka
- Raw Kafka Consumer API usage
- At-least-once delivery

**Problems:**
- No duplicate detection
- Messages can be lost

---

### [Example 2: With Outbox, No Deduplication](./example-2/)
Introduces the Transactional Outbox pattern on the producer side.

**Demonstrates:**
- Transactional Outbox pattern
- Database-first event storage
- Background polling for event publishing
- Events published twice (intentionally) to demonstrate duplicate issue

**Improvements over Example 1:**
- ✅ No message loss
- ✅ Event ordering preserved

**Remaining Problems:**
- ❌ Duplicate messages processed multiple times

---

### [Example 3: With Outbox, Deduplication](./example-3/)
Shows a **common pitfall** when implementing deduplication.

**Demonstrates:**
- Attempt at idempotent consumer pattern
- Deduplication table

---

### [Example 4: With Outbox, Deduplication](./example-4/)

**Demonstrates:**
- Idempotent consumer pattern


**Benefits:**
- ✅ No message loss
- ✅ No duplicate processing

---

## Architecture Patterns

### Transactional Outbox Pattern (Producer)
```
┌─────────────┐
│  Business   │
│   Logic     │
└──────┬──────┘
       │
       ▼
┌─────────────┐     ┌──────────────┐
│  Database   │────▶│ Outbox Table │
└─────────────┘     └──────┬───────┘
                           │ (polling)
                           ▼
                    ┌──────────────┐
                    │    Kafka     │
                    └──────────────┘
```

**Key Points:**
- Events stored in database first
- Background poller publishes to Kafka
- Atomic with business logic
- Never loses events

### Idempotent Consumer Pattern (Consumer)
```
┌──────────────┐
│    Kafka     │
└──────┬───────┘
       │
       ▼
┌─────────────────────────────────┐
│    Single Transaction           │
│  ┌─────────────────────────┐   │
│  │ 1. Check Message ID     │   │
│  │ 2. Record Message ID    │   │
│  │ 3. Process Event        │   │
│  │ 4. Save Results         │   │
│  └─────────────────────────┘   │
└─────────────────────────────────┘
       │
       ▼
  All or Nothing!
```

**Key Points:**
- Everything in one transaction
- Duplicate check + business logic
- Atomic commit or rollback
- Safe retries

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Apache Kafka**: 3.6.1 (via Confluent Docker images)
- **PostgreSQL**: 16
- **Protocol Buffers**: 3.25.1
- **Docker & Docker Compose**

## Project Structure

```
claude-workshop/
├── README.md (this file)
├── example-1/
│   ├── README.md
│   ├── pom.xml
│   ├── producer/
│   ├── consumer/
│   ├── docker-compose.yml
│   └── init-db.sql
├── example-2/
│   └── (same structure)
├── example-3/
│   └── (same structure)
└── example-4/
    └── (same structure)
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.9** or higher
- **Docker** and **Docker Compose**
- Basic understanding of:
  - Spring Boot
  - Kafka concepts (topics, producers, consumers)
  - Database transactions
  - Event-driven architecture

## Getting Started

Each example is self-contained and can be run independently:

1. Navigate to an example directory (e.g., `cd example-1`)
2. Read the example's README.md
3. Build: `mvn clean package`
4. Start infrastructure: `docker-compose up -d kafka postgres`
5. Run producer: `cd producer && mvn spring-boot:run`
6. Run consumer: `cd consumer && mvn spring-boot:run`
7. Observe and experiment!

## Learning Path

We recommend going through the examples in order:

1. **Start with Example 1** - Understand the baseline
2. **Move to Example 2** - Learn the Outbox pattern
3. **Study Example 3** - Understand what can go wrong
4. **Finish with Example 4** - Learn the correct pattern

## Key Takeaways

1. **Use Transactional Outbox** for reliable event publishing
2. **Use Idempotent Consumers** for duplicate handling
3. **Keep deduplication and business logic in the same transaction**
4. **Test failure scenarios** - simulate failures to verify behavior


## Additional Resources

- [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [Idempotent Consumer Pattern](https://microservices.io/patterns/communication-style/idempotent-consumer.html)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)


## License

This project is provided as-is for educational purposes.
