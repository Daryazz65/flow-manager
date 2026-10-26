package com.example.flow_manager.messaging;

import com.example.flow_manager.dto.SubscriptionChangedEvent;
import com.example.flow_manager.exception.EventDeserializationException;
import com.example.flow_manager.subscription.SubscriptionCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionChangedListener {

    private final ObjectMapper objectMapper;
    private final SubscriptionCacheService subscriptionCacheService;

    @KafkaListener(
            topics = "${app.topics.subscription-changed}",
            groupId = "${app.subscription.consumer-group}"
    )
    public void handle(String payload) {
        SubscriptionChangedEvent event = deserialize(payload);

        subscriptionCacheService.evict(event.userLogin());
    }

    private SubscriptionChangedEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(
                    payload,
                    SubscriptionChangedEvent.class
            );
        } catch (JsonProcessingException exception) {
            throw new EventDeserializationException(exception);
        }
    }
}