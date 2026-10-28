
package com.example.postapi;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    private final EventIdempotencyService idempotencyService;

    public EventConsumer(EventIdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(
        topics = "events-topic",
        groupId = "postapi-group"
    )
    public void consumeEvent(EventMessage event) {
        System.out.println("Received event: " + event.getEventId());

        if (idempotencyService.markAsProcessed(event.getEventId())) {
            System.out.println("Message: " + event.getMessage());

            if (event.isShouldFail()) {
                throw new RuntimeException(
                    "Simulated processing failure"
                );
            }

            System.out.println("Event processed successfully!");
        } else {
            System.out.println(
                "Duplicate event ignored: " + event.getEventId()
            );
        }
    }
}
