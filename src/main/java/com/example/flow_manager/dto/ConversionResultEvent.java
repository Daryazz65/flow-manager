package com.example.flow_manager.dto;

import com.example.flow_manager.domain.ConversionResultStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversionResultEvent(
        UUID eventId,
        UUID sourceEventId,
        ConversionResultStatus status,
        List<ConvertedFileDto> files,
        String errorMessage,
        Instant occurredAt
) {
}