# Example 3: With Transactional Outbox, Deduplication

## Overview

This example demonstrates deduplication
- **Producer**: Same as Example 2 (outbox pattern with duplicate publishing)
- **Consumer**: Attempts deduplication


## Characteristics

### Producer
- Same as Example 2 (transactional outbox)
- Publishes each event twice

### Consumer
- Tracks message IDs in `processed_message_ids` table

## Running the Example

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker and Docker Compose

### Steps

1. **Build the projects**:
   ```bash
   cd example-3
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
   - Look for "SIMULATED FAILURE" messages
   - Look for "DUPLICATE DETECTED" messages
   - Notice that some events are marked as duplicates but were never processed successfully!

7. **Check the database to see the data loss**:
   ```bash
   docker exec -it example-3-postgres-1 psql -U workshop -d workshop

   # Count message IDs tracked for deduplication
   SELECT COUNT(*) FROM consumer_schema.processed_message_ids;

   # Count actually processed operations
   SELECT COUNT(*) FROM consumer_schema.processed_operations;

   # Find LOST messages (tracked but not processed)
   SELECT pmi.message_id, pmi.processed_at
   FROM consumer_schema.processed_message_ids pmi
   LEFT JOIN consumer_schema.processed_operations po ON pmi.message_id = po.message_id
   WHERE po.id IS NULL
   ORDER BY pmi.processed_at;
   ```

8. **Stop everything**:
   ```bash
   docker-compose down -v
   ```

## Questions

What's the problem with this implementation? What happens when processing fails after deduplication check?

