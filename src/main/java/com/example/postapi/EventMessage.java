package com.example.postapi;

public class EventMessage {

    private String eventId;
    private String message;
    private boolean shouldFail;

    public EventMessage() {
    }

    public EventMessage(String eventId, String message, boolean shouldFail) {
        this.eventId = eventId;
        this.message = message;
        this.shouldFail = shouldFail;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShouldFail() {
        return shouldFail;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}