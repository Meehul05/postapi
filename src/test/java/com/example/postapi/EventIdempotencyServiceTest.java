
package com.example.postapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventIdempotencyServiceTest {

    private EventIdempotencyService service;

    @BeforeEach
    void setUp() {
        service = new EventIdempotencyService();
    }

    @Test
    void shouldAcceptNewEventId() {
        assertTrue(service.markAsProcessed("evt-001"));
    }

    @Test
    void shouldRejectDuplicateEventId() {
        service.markAsProcessed("evt-001");

        assertFalse(service.markAsProcessed("evt-001"));
    }

    @Test
    void shouldAcceptDifferentEventIds() {
        assertTrue(service.markAsProcessed("evt-001"));
        assertTrue(service.markAsProcessed("evt-002"));
    }
}
