package com.example.flow_manager.messaging;

import com.example.flow_manager.config.AppProperties;
import com.example.flow_manager.dto.SubscriptionChangedEvent;
import com.example.flow_manager.exception.EventDeserializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionChangedListener {

    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private final AppProperties properties;

    @KafkaListener(
            topics = "${app.topics.subscription-changed}",
            groupId = "flow-manager-subscription-cache"
    )
    public void handle(String payload) {
        SubscriptionChangedEvent event = deserialize(payload);

        Cache cache = cacheManager.getCache(
                properties.subscription().cacheName()
        );

        if (cache != null) {
            cache.evict(event.userLogin());
        }
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