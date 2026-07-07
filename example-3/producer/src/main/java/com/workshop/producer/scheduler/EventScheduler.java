package com.workshop.producer.scheduler;

import com.workshop.producer.service.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventScheduler {
    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);

    private final EventProducer eventProducer;

    public EventScheduler(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @Scheduled(fixedRate = 1000)  // Run every second
    public void generateEvent() {
        log.debug("Scheduled job triggered - producing event");
        eventProducer.produceEvent();
    }
}
