package com.example.flow_manager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.flow_manager.config.AppProperties;
import com.example.flow_manager.exception.FileSizeLimitExceededException;
import com.example.flow_manager.repository.FileProcessingRepository;
import com.example.flow_manager.repository.OutboxMessageRepository;
import com.example.flow_manager.repository.ProcessedMessageRepository;
import com.example.flow_manager.service.FileProcessingService;
import com.example.flow_manager.storage.ObjectStorage;
import com.example.flow_manager.subscription.SubscriptionAccessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    private static final String USER_LOGIN = "test-user";
    private static final long MAX_FILE_SIZE = 104_857_600L;
    private static final long FILE_SIZE = 104_857_601L;

    @Mock
    private FileProcessingRepository fileProcessingRepository;

    @Mock
    private ProcessedMessageRepository processedMessageRepository;

    @Mock
    private OutboxMessageRepository outboxMessageRepository;

    @Mock
    private ObjectStorage storage;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SubscriptionAccessService subscriptionAccessService;

    @Mock
    private MultipartFile file;

    @Test
    void doesNotStoreFileWhenSubscriptionLimitExceeded() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("large.pdf");
        when(file.getSize()).thenReturn(FILE_SIZE);

        doThrow(new FileSizeLimitExceededException(MAX_FILE_SIZE))
                .when(subscriptionAccessService)
                .validateUpload(USER_LOGIN, FILE_SIZE);

        FileProcessingService service = new FileProcessingService(
                fileProcessingRepository,
                processedMessageRepository,
                outboxMessageRepository,
                storage,
                objectMapper,
                properties(),
                subscriptionAccessService
        );

        assertThatThrownBy(() -> service.upload(USER_LOGIN, file))
                .isInstanceOf(FileSizeLimitExceededException.class);

        verifyNoInteractions(
                fileProcessingRepository,
                processedMessageRepository,
                outboxMessageRepository,
                storage,
                objectMapper
        );
    }

    private AppProperties properties() {
        return new AppProperties(
                new AppProperties.Minio(
                        "http://localhost:9000",
                        "minioadmin",
                        "minioadmin",
                        "files"
                ),
                new AppProperties.Topics(
                        "file.conversion.requested",
                        "file.conversion.result",
                        "subscription.changed"
                ),
                new AppProperties.Subscription(
                        "subscription-limits"
                ),
                new AppProperties.Outbox(1000, 100)
        );
    }
}