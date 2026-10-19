package com.example.flow_manager.dto;

import com.example.flow_manager.domain.ProcessingStatus;
import java.util.UUID;

public record FileUploadResponse(
        UUID id,
        ProcessingStatus status
) {
}