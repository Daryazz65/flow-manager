package com.example.flow_manager.exception;

public class SubscriptionServiceException extends FlowManagerException {

    public SubscriptionServiceException(String message) {
        super(message);
    }

    public SubscriptionServiceException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}