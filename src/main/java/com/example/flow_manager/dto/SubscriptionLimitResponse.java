package com.example.flow_manager.dto;

import com.example.flow_manager.domain.SubscriptionType;
import java.io.Serializable;
import java.time.Instant;

public record SubscriptionLimitResponse(
        String userLogin,
        SubscriptionType subscriptionType,
        Instant expiresAt,
        long maxFileSizeBytes
) implements Serializable {
}