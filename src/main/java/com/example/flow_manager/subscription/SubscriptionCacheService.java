package com.example.flow_manager.subscription;

import com.example.flow_manager.dto.SubscriptionLimitResponse;
import com.example.flow_manager.exception.SubscriptionServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionCacheService {

    private final SubscriptionClient subscriptionClient;

    @Cacheable(
            cacheNames = "${app.subscription.cache-name}",
            key = "#userLogin"
    )
    public SubscriptionLimitResponse getLimit(String userLogin) {
        try {
            return subscriptionClient.getLimit(userLogin);
        } catch (FeignException exception) {
            throw new SubscriptionServiceException(
                    "Cannot obtain subscription information for user: " + userLogin,
                    exception
            );
        }
    }

    @CacheEvict(
            cacheNames = "${app.subscription.cache-name}",
            key = "#userLogin"
    )
    public void evict(String userLogin) {
    }
}