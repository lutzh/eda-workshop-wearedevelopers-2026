# Example 2: With Transactional Outbox

## Overview

This example demonstrates the **Transactional Outbox Pattern**:
- **Producer**: Saves events to an outbox table in the database, then a background poller publishes them to Kafka
- **Consumer**: Consumes events and stores them in the database

## Characteristics

### Producer
- Events are first saved to an `outbox_events` table in a transaction
- A background poller (every 3 seconds) reads pending events and publishes them to Kafka
- (Each event is published TWICE to demonstrate the need for deduplication)
- Provides publishing guarantees: events are never lost
- Events are published in order (maintains ordering)

### Consumer
- Consumes events from Kafka using the raw Kafka Consumer API
- Stores processed events in `processed_operations` table
- Commits Kafka offsets only after successful database storage

## Question

What are the potential issues with this implementation?


## Project Structure

```
example-2/
├── pom.xml (parent POM)
├── producer/
│   ├── pom.xml
│   └── src/main/java/com/workshop/producer/
│       ├── ProducerApplication.java
│       ├── entity/OutboxEvent.java
│       ├── repository/OutboxEventRepository.java
│       ├── config/KafkaConfig.java
│       ├── service/
│       │   ├── EventProducer.java (saves to outbox)
│       │   └── OutboxPoller.java (publishes to Kafka)
│       └── scheduler/EventScheduler.java
├── consumer/
│   └── (same as Example 1)
├── docker-compose.yml
└── init-db.sql
```

## Running the Example

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker and Docker Compose

### Steps

1. **Build the projects**:
   ```bash
   cd example-2
   mvn clean package
   ```

2. **Start the infrastructure**:
   ```bash
   docker-compose up -d kafka postgres
   ```

3. **Wait for services to be ready** (~30 seconds)

4. **Run the producer**:
   ```bash
   cd producer
   mvn spring-boot:run
   ```

5. **Run the consumer** (in a separate terminal):
   ```bash
   cd consumer
   mvn spring-boot:run
   ```

6. **Observe the logs**:
   - Producer creates one event per second and saves to outbox
   - Outbox poller publishes events every 3 seconds (TWICE each)
   - Consumer processes duplicate messages

7. **Check the database**:
   ```bash
   docker exec -it example-2-postgres-1 psql -U workshop -d workshop

   # Check outbox events
   SELECT COUNT(*) FROM producer_schema.outbox_events;
   SELECT status, COUNT(*) FROM producer_schema.outbox_events GROUP BY status;

   # Check processed operations (you'll see DUPLICATES!)
   SELECT COUNT(*) FROM consumer_schema.processed_operations;
   SELECT message_id, COUNT(*) as count
   FROM consumer_schema.processed_operations
   GROUP BY message_id
   HAVING COUNT(*) > 1;
   ```

8. **Stop everything**:
   ```bash
   docker-compose down -v
   ```

