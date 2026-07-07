# Example 4: With Transactional Outbox, Deduplication

## Overview

This example demonstrates the **deduplication pattern**:
- **Producer**: Same as Examples 2-3 (outbox pattern with duplicate publishing)
- **Consumer**: Implements deduplication

## The Approach

The consumer tracks message IDs in a `processed_message_ids` table, and it checks/records the message ID in the same transaction as the business logic.

### How It Works

```
1. Message "abc-123" arrives
2. Transaction starts:
   - Check if "abc-123" exists → No
   - Insert "abc-123" into processed_message_ids
   - Process business logic
   - Save to processed_operations
   - If everything succeeds: COMMIT ✓
   - If anything fails: ROLLBACK (including message ID!) ✗
3. If Transaction committed:
   - Kafka offset committed
   - Message successfully processed
4. If Transaction rolled back:
   - Message ID was NOT persisted
   - Kafka offset NOT committed
   - Message will be redelivered
5. Message "abc-123" arrives again (retry after rollback)
6. Transaction starts:
   - Check if "abc-123" exists → No (was rolled back!)
   - Process again (will succeed this time)
   - COMMIT ✓
```



## Characteristics

### Producer
- Same as Examples 2-3 (transactional outbox)
- Publishes each event twice to test deduplication

### Consumer
- Tracks message IDs in `processed_message_ids` table
- Deduplication check, ID insert, and business logic all in one transaction
- Every 10th event fails to demonstrate correct retry behavior

## Running the Example

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker and Docker Compose

### Steps

1. **Build the projects**:
   ```bash
   cd example-4
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
   - Look for "DUPLICATE DETECTED" messages (from the intentional duplicates)
   - Notice that failed events are retried and processed successfully
   - Notice that intentional duplicates (from publishing twice) are correctly skipped

7. **Check the database to verify NO data loss**:
   ```bash
   docker exec -it example-4-postgres-1 psql -U workshop -d workshop

   # Count message IDs tracked for deduplication
   SELECT COUNT(*) FROM consumer_schema.processed_message_ids;

   # Count actually processed operations
   SELECT COUNT(*) FROM consumer_schema.processed_operations;

   # These should be EQUAL (no data loss)
   # Verify no messages are tracked but not processed
   SELECT COUNT(*)
   FROM consumer_schema.processed_message_ids pmi
   LEFT JOIN consumer_schema.processed_operations po ON pmi.message_id = po.message_id
   WHERE po.id IS NULL;

   # Should return 0 (no lost messages!)
   ```

8. **Stop everything**:
   ```bash
   docker-compose down -v
   ```

## Questions

Speeding up deduplication, e.g. with Bloom filters. Consequences, new risks, etc.

