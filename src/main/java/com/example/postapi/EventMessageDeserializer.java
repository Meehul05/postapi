
package com.example.postapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class EventMessageDeserializer implements Deserializer<EventMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public EventMessage deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, EventMessage.class);
        } catch (Exception e) {
            throw new SerializationException(
                "Failed to deserialize event message", e
            );
        }
    }
}
