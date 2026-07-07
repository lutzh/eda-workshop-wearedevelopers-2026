# Example 1

## Overview

This example demonstrates the simplest event-driven architecture pattern:
- **Producer**: Publishes events to Kafka 
- **Consumer**: Consumes events and stores them in the database

[## Question

Focus on the producer side. What are the potential issues with this producer implementation?
]()
## Project Structure

```
example-1/
├── pom.xml (parent POM)
├── producer/
│   ├── pom.xml
│   └── src/main/java/com/workshop/producer/
│       ├── ProducerApplication.java
│       ├── config/KafkaConfig.java
│       ├── service/EventProducer.java
│       └── scheduler/EventScheduler.java
├── consumer/
│   ├── pom.xml
│   └── src/main/java/com/workshop/consumer/
│       ├── ConsumerApplication.java
│       ├── entity/ProcessedOperation.java
│       ├── repository/ProcessedOperationRepository.java
│       ├── service/EventProcessor.java
│       └── kafka/KafkaEventConsumer.java
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
   cd example-1
   mvn clean package
   ```

2. **Start the infrastructure** (Kafka, PostgreSQL):
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
   - Producer creates one event per second
   - Consumer processes events and stores them in the database

7. **Check the database**:
   ```bash
   docker exec -it example-1-postgres-1 psql -U workshop -d workshop
   \dt consumer_schema.*
   SELECT COUNT(*) FROM consumer_schema.processed_operations;
   SELECT * FROM consumer_schema.processed_operations ORDER BY processed_at DESC LIMIT 10;
   ```

8. **Stop everything**:
   ```bash
   docker-compose down -v
   ```

