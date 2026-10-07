package com.example.flow_manager.repository;

import com.example.flow_manager.domain.FileProcessing;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileProcessingRepository extends JpaRepository<FileProcessing, UUID> {
}