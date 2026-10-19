package com.example.flow_manager.dto;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String message
) {
}