package com.example.flow_manager.repository;

import com.example.flow_manager.domain.ProcessedMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository
        extends JpaRepository<ProcessedMessage, UUID> {
}