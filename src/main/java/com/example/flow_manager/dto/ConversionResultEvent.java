package com.example.flow_manager.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversionResultEvent(
        UUID eventId,
        UUID sourceEventId,
        String status,
        List<ConvertedFileDto> files,
        String errorMessage,
        Instant occurredAt
) {
}