package com.example.flow_manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    private static final int PARTITIONS = 3;
    private static final int REPLICAS = 1;

    @Bean
    public NewTopic conversionRequestedTopic(AppProperties properties) {
        return createTopic(properties.topics().conversionRequested());
    }

    @Bean
    public NewTopic conversionResultTopic(AppProperties properties) {
        return createTopic(properties.topics().conversionResult());
    }

    @Bean
    public NewTopic subscriptionChangedTopic(AppProperties properties) {
        return createTopic(properties.topics().subscriptionChanged());
    }

    private NewTopic createTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .build();
    }
}