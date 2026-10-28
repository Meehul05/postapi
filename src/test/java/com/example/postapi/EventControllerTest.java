
package com.example.postapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest {

    private MockMvc mockMvc;
    private EventProducer eventProducer;

    @BeforeEach
    void setUp() {
        eventProducer = mock(EventProducer.class);

        EventController controller = new EventController(eventProducer);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldSendEventSuccessfully() throws Exception {
        mockMvc.perform(post("/api/events")
                .contentType("application/json")
                .content("""
                    {
                        "eventId": "evt-test-001",
                        "message": "MockMvc integration test",
                        "shouldFail": false
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string(
                    org.hamcrest.Matchers.containsString("evt-test-001")
                ));

        verify(eventProducer, times(1)).sendEvent(
                argThat(event -> "evt-test-001".equals(event.getEventId()))
        );
    }

    @Test
    void shouldGenerateEventIdWhenMissing() throws Exception {
        mockMvc.perform(post("/api/events")
                .contentType("application/json")
                .content("""
                    {
                        "message": "Generated ID test",
                        "shouldFail": false
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string(
                    org.hamcrest.Matchers.containsString("Event sent:")
                ));

        verify(eventProducer, times(1)).sendEvent(
                argThat(event -> event.getEventId() != null
                        && !event.getEventId().isBlank())
        );
    }
}
