package com.example.flow_manager.messaging;

import com.example.flow_manager.config.AppProperties;
import com.example.flow_manager.domain.OutboxMessage;
import com.example.flow_manager.exception.FlowManagerException;
import com.example.flow_manager.repository.OutboxMessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AppProperties properties;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay}")
    @SchedulerLock(
            name = "flow-manager-outbox-poller",
            lockAtLeastFor = "PT1S",
            lockAtMostFor = "PT30S"
    )
    @Transactional
    public void publish() {
        List<OutboxMessage> messages =
                outboxMessageRepository.findUnpublishedForUpdate(
                        PageRequest.of(0, properties.outbox().batchSize())
                );

        for (OutboxMessage message : messages) {
            publish(message);
        }
    }

    private void publish(OutboxMessage message) {
        try {
            kafkaTemplate.send(
                    message.getTopic(),
                    message.getId().toString(),
                    message.getPayload()
            ).get();

            message.markPublished();
        } catch (Exception exception) {
            throw new FlowManagerException(
                    "Cannot publish outbox message " + message.getId(),
                    exception
            );
        }
    }
}