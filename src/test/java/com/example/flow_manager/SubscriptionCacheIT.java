package com.example.flow_manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.flow_manager.domain.SubscriptionType;
import com.example.flow_manager.dto.SubscriptionChangedEvent;
import com.example.flow_manager.dto.SubscriptionLimitResponse;
import com.example.flow_manager.messaging.SubscriptionChangedListener;
import com.example.flow_manager.subscription.SubscriptionAccessService;
import com.example.flow_manager.subscription.SubscriptionClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

class SubscriptionCacheIT extends AbstractIT {

    private static final String USER_LOGIN = "test-user";

    @Autowired
    private SubscriptionAccessService subscriptionAccessService;

    @Autowired
    private SubscriptionChangedListener subscriptionChangedListener;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private SubscriptionClient subscriptionClient;

    @BeforeEach
    void clearCache() {
        Cache cache = cacheManager.getCache("subscription-limits");

        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void cachesSubscriptionLimit() {
        SubscriptionLimitResponse expected = subscriptionLimit();

        when(subscriptionClient.getLimit(USER_LOGIN)).thenReturn(expected);

        SubscriptionLimitResponse first = subscriptionAccessService.getLimit(
                USER_LOGIN
        );
        SubscriptionLimitResponse second = subscriptionAccessService.getLimit(
                USER_LOGIN
        );

        assertThat(first).isEqualTo(expected);
        assertThat(second).isEqualTo(expected);

        verify(subscriptionClient, times(1)).getLimit(USER_LOGIN);
    }

    @Test
    void evictsSubscriptionLimitAfterSubscriptionChangedEvent()
            throws JsonProcessingException {
        SubscriptionLimitResponse expected = subscriptionLimit();

        when(subscriptionClient.getLimit(USER_LOGIN)).thenReturn(expected);

        subscriptionAccessService.getLimit(USER_LOGIN);

        SubscriptionChangedEvent event = new SubscriptionChangedEvent(
                UUID.randomUUID(),
                USER_LOGIN,
                SubscriptionType.FREE,
                null
        );

        subscriptionChangedListener.handle(
                objectMapper.writeValueAsString(event)
        );

        subscriptionAccessService.getLimit(USER_LOGIN);

        verify(subscriptionClient, times(2)).getLimit(USER_LOGIN);
    }

    private SubscriptionLimitResponse subscriptionLimit() {
        return new SubscriptionLimitResponse(
                USER_LOGIN,
                SubscriptionType.PAID,
                Instant.parse("2026-12-31T23:59:59Z"),
                Long.MAX_VALUE
        );
    }
}