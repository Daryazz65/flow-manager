package com.example.flow_manager.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
public class ProcessedMessage {

    @Id
    private UUID id;

    private Instant processedAt = Instant.now();

    protected ProcessedMessage() {
    }

    public ProcessedMessage(UUID id) {
        this.id = id;
    }
}