package com.example.flow_manager.subscription;

import com.example.flow_manager.dto.SubscriptionLimitResponse;
import com.example.flow_manager.exception.FileSizeLimitExceededException;
import com.example.flow_manager.exception.SubscriptionServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SubscriptionAccessService {

    private final SubscriptionCacheService subscriptionCacheService;

    public void validateUpload(String userLogin, long fileSize) {
        if (!StringUtils.hasText(userLogin)) {
            throw new SubscriptionServiceException(
                    "User login header must not be empty"
            );
        }

        SubscriptionLimitResponse subscription =
                subscriptionCacheService.getLimit(userLogin);

        if (fileSize > subscription.maxFileSizeBytes()) {
            throw new FileSizeLimitExceededException(
                    subscription.maxFileSizeBytes()
            );
        }
    }
}