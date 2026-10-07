package com.example.flow_manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FileProcessing {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String sourceBucket;

    @Column(nullable = false)
    private String sourceObjectKey;

    private String resultBucket;

    private String resultObjectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status;

    private String errorMessage;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected FileProcessing() {
    }

    public FileProcessing(
            UUID id,
            String originalFileName,
            String sourceBucket,
            String sourceObjectKey
    ) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.sourceBucket = sourceBucket;
        this.sourceObjectKey = sourceObjectKey;
        this.status = ProcessingStatus.PROCESSING;
    }

    public void markSuccess(String resultBucket, String resultObjectKey) {
        this.resultBucket = resultBucket;
        this.resultObjectKey = resultObjectKey;
        this.status = ProcessingStatus.SUCCESS;
        this.errorMessage = null;
        this.updatedAt = Instant.now();
    }

    public void markError(String errorMessage) {
        this.status = ProcessingStatus.ERROR;
        this.errorMessage = errorMessage;
        this.updatedAt = Instant.now();
    }
}