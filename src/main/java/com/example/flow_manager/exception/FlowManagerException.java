package com.example.flow_manager.exception;

public class FlowManagerException extends RuntimeException {

    public FlowManagerException(String message) {
        super(message);
    }

    public FlowManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}