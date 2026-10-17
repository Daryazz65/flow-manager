package com.example.flow_manager.exception;

public class FileUploadException extends FlowManagerException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}