package com.example.flow_manager.messaging;

import com.example.flow_manager.dto.ConversionResultEvent;
import com.example.flow_manager.exception.EventDeserializationException;
import com.example.flow_manager.service.FileProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversionResultListener {

    private final FileProcessingService fileProcessingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.topics.conversion-result}")
    public void consume(String payload) {
        try {
            ConversionResultEvent event = objectMapper.readValue(
                    payload,
                    ConversionResultEvent.class
            );

            fileProcessingService.applyConversionResult(event);
        } catch (JsonProcessingException exception) {
            throw new EventDeserializationException(exception);
        }
    }
}