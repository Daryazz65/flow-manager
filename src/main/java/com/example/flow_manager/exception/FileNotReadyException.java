package com.example.flow_manager.exception;

import java.util.UUID;

public class FileNotReadyException extends FlowManagerException {

    public FileNotReadyException(UUID id) {
        super("Converted file is not ready: " + id);
    }
}