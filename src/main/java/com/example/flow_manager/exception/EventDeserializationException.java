package com.example.flow_manager.exception;

public class EventDeserializationException extends FlowManagerException {

    public EventDeserializationException(Throwable cause) {
        super("Cannot deserialize conversion result event", cause);
    }
}