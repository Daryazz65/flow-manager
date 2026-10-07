package com.example.flow_manager.exception;

import java.util.UUID;

public class FileProcessingNotFoundException extends FlowManagerException {

    public FileProcessingNotFoundException(UUID id) {
        super("File processing not found: " + id);
    }
}