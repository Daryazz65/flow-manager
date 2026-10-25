package com.example.flow_manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Minio minio,
        Topics topics,
        Subscription subscription,
        Outbox outbox
) {

    public record Minio(
            String endpoint,
            String accessKey,
            String secretKey,
            String bucket
    ) {
    }

    public record Topics(
            String conversionRequested,
            String conversionResult,
            String subscriptionChanged
    ) {
    }

    public record Subscription(
            String cacheName
    ) {
    }

    public record Outbox(
            long fixedDelay,
            int batchSize
    ) {
    }
}