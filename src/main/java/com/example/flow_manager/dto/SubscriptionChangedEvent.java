package com.example.flow_manager.dto;

import com.example.flow_manager.domain.SubscriptionType;
import java.time.Instant;
import java.util.UUID;

public record SubscriptionChangedEvent(
        UUID eventId,
        String userLogin,
        SubscriptionType subscriptionType,
        Instant occurredAt
) {
}