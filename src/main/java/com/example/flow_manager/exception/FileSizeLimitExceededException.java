package com.example.flow_manager.exception;

public class FileSizeLimitExceededException extends FlowManagerException {

    public FileSizeLimitExceededException(long maxFileSizeBytes) {
        super("File size exceeds allowed limit of " + maxFileSizeBytes + " bytes");
    }
}