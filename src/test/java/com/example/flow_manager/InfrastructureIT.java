package com.example.flow_manager;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.flow_manager.repository.FileProcessingRepository;
import com.example.flow_manager.repository.OutboxMessageRepository;
import com.example.flow_manager.repository.ProcessedMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InfrastructureIT extends AbstractIT {

    @Autowired
    private FileProcessingRepository fileProcessingRepository;

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Autowired
    private ProcessedMessageRepository processedMessageRepository;

    @Test
    void startsContainersAndAppliesMigrations() {
        assertThat(fileProcessingRepository.count()).isZero();
        assertThat(outboxMessageRepository.count()).isZero();
        assertThat(processedMessageRepository.count()).isZero();
    }
}