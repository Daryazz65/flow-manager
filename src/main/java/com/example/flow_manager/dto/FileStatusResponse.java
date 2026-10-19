package com.example.flow_manager.dto;

import com.example.flow_manager.domain.ProcessingStatus;
import java.util.UUID;

public record FileStatusResponse(
        UUID id,
        String originalFileName,
        ProcessingStatus status,
        String errorMessage
) {
}