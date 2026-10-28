
package com.example.postapi;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventIdempotencyService {

    private final Set<String> processedEventIds =
            ConcurrentHashMap.newKeySet();

    public boolean markAsProcessed(String eventId) {
        return processedEventIds.add(eventId);
    }
}
