package com.example.flow_manager.subscription;

import com.example.flow_manager.dto.SubscriptionLimitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "subscription-service",
        path = "/api/v1/subscriptions"
)
public interface SubscriptionClient {

    @GetMapping("/{userLogin}/limit")
    SubscriptionLimitResponse getLimit(
            @PathVariable String userLogin
    );
}