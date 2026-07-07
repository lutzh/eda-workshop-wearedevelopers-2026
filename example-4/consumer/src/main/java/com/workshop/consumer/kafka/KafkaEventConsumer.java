package com.workshop.consumer.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.workshop.consumer.service.EventProcessor;
import com.workshop.event.OperationCompletedProto.OperationCompleted;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class KafkaEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final EventProcessor eventProcessor;
    private final String bootstrapServers;
    private final String topicName;
    private final String groupId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private KafkaConsumer<String, byte[]> consumer;

    public KafkaEventConsumer(EventProcessor eventProcessor,
                              @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                              @Value("${kafka.topic.operation-completed}") String topicName,
                              @Value("${kafka.consumer.group-id}") String groupId) {
        this.eventProcessor = eventProcessor;
        this.bootstrapServers = bootstrapServers;
        this.topicName = topicName;
        this.groupId = groupId;
    }

    @PostConstruct
    public void start() {
        log.info("Starting Kafka consumer for topic: {}", topicName);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));

        running.set(true);
        executor.submit(this::pollLoop);
    }

    private void pollLoop() {
        try {
            while (running.get()) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, byte[]> record : records) {
                    boolean processed = false;
                    while (!processed && running.get()) {
                        try {
                            OperationCompleted event = OperationCompleted.parseFrom(record.value());
                            eventProcessor.processEvent(event);
                            consumer.commitAsync();
                            processed = true;
                        } catch (Exception e) {
                            log.error("Error processing message, will retry...", e);
                            /* In a real application, implement a backoff strategy
                             */
                            try {
                                Thread.sleep(1000); // this is not serious, just for demo purposes
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in poll loop", e);
        } finally {
            consumer.close();
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping Kafka consumer");
        running.set(false);
        executor.shutdown();
    }
}
