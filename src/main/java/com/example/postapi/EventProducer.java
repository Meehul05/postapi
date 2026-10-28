package com.example.postapi;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private final KafkaTemplate<String, EventMessage> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, EventMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(EventMessage event) {
        kafkaTemplate.send("events-topic", event.getEventId(), event);
        System.out.println("Event sent: " + event.getEventId());
    }
}