-- Create schemas
CREATE SCHEMA IF NOT EXISTS producer_schema;
CREATE SCHEMA IF NOT EXISTS consumer_schema;

-- Consumer schema tables
CREATE TABLE IF NOT EXISTS consumer_schema.processed_operations (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(255) NOT NULL,
    info TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_processed_operations_message_id ON consumer_schema.processed_operations(message_id);
CREATE INDEX idx_processed_operations_processed_at ON consumer_schema.processed_operations(processed_at);
