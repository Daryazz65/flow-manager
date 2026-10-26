package com.example.flow_manager.service;

import com.example.flow_manager.config.AppProperties;
import com.example.flow_manager.domain.ConversionResultStatus;
import com.example.flow_manager.domain.EventType;
import com.example.flow_manager.domain.FileProcessing;
import com.example.flow_manager.domain.OutboxMessage;
import com.example.flow_manager.domain.ProcessedMessage;
import com.example.flow_manager.domain.ProcessingStatus;
import com.example.flow_manager.dto.ConversionRequestedEvent;
import com.example.flow_manager.dto.ConversionResultEvent;
import com.example.flow_manager.dto.ConvertedFileDto;
import com.example.flow_manager.dto.DownloadedFile;
import com.example.flow_manager.dto.FileStatusResponse;
import com.example.flow_manager.dto.FileUploadResponse;
import com.example.flow_manager.exception.EventSerializationException;
import com.example.flow_manager.exception.FileNotReadyException;
import com.example.flow_manager.exception.FileProcessingNotFoundException;
import com.example.flow_manager.exception.FileUploadException;
import com.example.flow_manager.exception.InvalidConversionResultException;
import com.example.flow_manager.repository.FileProcessingRepository;
import com.example.flow_manager.repository.OutboxMessageRepository;
import com.example.flow_manager.repository.ProcessedMessageRepository;
import com.example.flow_manager.storage.ObjectStorage;
import com.example.flow_manager.subscription.SubscriptionAccessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private static final String INPUT_PREFIX = "input/";

    private final FileProcessingRepository fileProcessingRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectStorage storage;
    private final ObjectMapper objectMapper;
    private final AppProperties properties;
    private final SubscriptionAccessService subscriptionAccessService;

    @Transactional
    public FileUploadResponse upload(
            String userLogin,
            MultipartFile file
    ) {
        validateFile(file);
        subscriptionAccessService.validateUpload(userLogin, file.getSize());

        UUID fileId = UUID.randomUUID();
        String fileName = extractFileName(file.getOriginalFilename());
        String bucket = properties.minio().bucket();
        String objectKey = INPUT_PREFIX + fileId + "/" + fileName;

        storage.upload(bucket, objectKey, readContent(file), resolveContentType(file));

        FileProcessing fileProcessing = new FileProcessing(
                fileId,
                fileName,
                bucket,
                objectKey
        );

        fileProcessingRepository.save(fileProcessing);

        ConversionRequestedEvent event = new ConversionRequestedEvent(
                fileId,
                bucket,
                objectKey,
                fileName
        );

        saveOutboxMessage(fileId, event);

        return new FileUploadResponse(fileId, fileProcessing.getStatus());
    }

    @Transactional(readOnly = true)
    public FileStatusResponse getStatus(UUID fileId) {
        FileProcessing fileProcessing = findFileProcessing(fileId);

        return new FileStatusResponse(
                fileProcessing.getId(),
                fileProcessing.getOriginalFileName(),
                fileProcessing.getStatus(),
                fileProcessing.getErrorMessage()
        );
    }

    @Transactional(readOnly = true)
    public DownloadedFile downloadResult(UUID fileId) {
        FileProcessing fileProcessing = findFileProcessing(fileId);

        if (fileProcessing.getStatus() != ProcessingStatus.SUCCESS) {
            throw new FileNotReadyException(fileId);
        }

        byte[] content = storage.download(
                fileProcessing.getResultBucket(),
                fileProcessing.getResultObjectKey()
        );

        return new DownloadedFile(
                extractFileName(fileProcessing.getResultObjectKey()),
                content
        );
    }

    @Transactional
    public void applyConversionResult(ConversionResultEvent event) {
        if (processedMessageRepository.existsById(event.eventId())) {
            return;
        }

        FileProcessing fileProcessing = fileProcessingRepository
                .findByIdForUpdate(event.sourceEventId())
                .orElseThrow(
                        () -> new FileProcessingNotFoundException(
                                event.sourceEventId()
                        )
                );

        if (fileProcessing.getStatus() == ProcessingStatus.PROCESSING) {
            if (event.status() == ConversionResultStatus.SUCCESS) {
                applySuccessResult(fileProcessing, event.files());
            } else {
                fileProcessing.markError(event.errorMessage());
            }
        }

        processedMessageRepository.save(new ProcessedMessage(event.eventId()));
    }

    private void applySuccessResult(
            FileProcessing fileProcessing,
            List<ConvertedFileDto> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new InvalidConversionResultException(
                    "Successful conversion result does not contain files"
            );
        }

        ConvertedFileDto convertedFile = files.get(0);

        fileProcessing.markSuccess(
                convertedFile.bucket(),
                convertedFile.objectKey()
        );
    }

    private void saveOutboxMessage(
            UUID fileId,
            ConversionRequestedEvent event
    ) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxMessage outboxMessage = new OutboxMessage(
                    event.eventId(),
                    fileId,
                    EventType.FILE_CONVERSION_REQUESTED,
                    properties.topics().conversionRequested(),
                    payload
            );

            outboxMessageRepository.save(outboxMessage);
        } catch (JsonProcessingException exception) {
            throw new EventSerializationException(
                    "Cannot serialize conversion request event",
                    exception
            );
        }
    }

    private FileProcessing findFileProcessing(UUID fileId) {
        return fileProcessingRepository.findById(fileId)
                .orElseThrow(() -> new FileProcessingNotFoundException(fileId));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File must not be empty");
        }

        if (file.getOriginalFilename() == null
                || file.getOriginalFilename().isBlank()) {
            throw new FileUploadException("File name must not be empty");
        }
    }

    private String extractFileName(String fileName) {
        try {
            String normalizedFileName = Path.of(fileName)
                    .getFileName()
                    .toString();

            if (normalizedFileName.isBlank()) {
                throw new FileUploadException("File name must not be empty");
            }

            return normalizedFileName;
        } catch (RuntimeException exception) {
            if (exception instanceof FileUploadException) {
                throw exception;
            }

            throw new FileUploadException("Invalid file name", exception);
        }
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new FileUploadException(
                    "Cannot read uploaded file",
                    exception
            );
        }
    }

    private String resolveContentType(MultipartFile file) {
        return file.getContentType() == null
                ? "application/octet-stream"
                : file.getContentType();
    }
}