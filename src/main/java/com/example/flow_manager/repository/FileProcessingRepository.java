package com.example.flow_manager.repository;

import com.example.flow_manager.domain.FileProcessing;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileProcessingRepository
        extends JpaRepository<FileProcessing, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select fileProcessing
            from FileProcessing fileProcessing
            where fileProcessing.id = :id
            """)
    Optional<FileProcessing> findByIdForUpdate(@Param("id") UUID id);
}