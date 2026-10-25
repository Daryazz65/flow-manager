package com.example.flow_manager.subscription;

import com.example.flow_manager.dto.SubscriptionLimitResponse;
import com.example.flow_manager.exception.FileSizeLimitExceededException;
import com.example.flow_manager.exception.SubscriptionServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SubscriptionAccessService {

    private final SubscriptionClient subscriptionClient;

    public void validateUpload(String userLogin, long fileSize) {
        if (!StringUtils.hasText(userLogin)) {
            throw new SubscriptionServiceException(
                    "User login header must not be empty"
            );
        }

        SubscriptionLimitResponse subscription = getLimit(userLogin);

        if (fileSize > subscription.maxFileSizeBytes()) {
            throw new FileSizeLimitExceededException(
                    subscription.maxFileSizeBytes()
            );
        }
    }

    @Cacheable(
            cacheNames = "subscription-limits",
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
}