package com.example.flow_manager.controller;

import com.example.flow_manager.dto.ApiError;
import com.example.flow_manager.exception.FileNotReadyException;
import com.example.flow_manager.exception.FileProcessingNotFoundException;
import com.example.flow_manager.exception.FileUploadException;
import com.example.flow_manager.exception.FlowManagerException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(FileProcessingNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            FileProcessingNotFoundException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(FileNotReadyException.class)
    public ResponseEntity<ApiError> handleNotReady(
            FileNotReadyException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiError> handleUpload(
            FileUploadException exception
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(FlowManagerException.class)
    public ResponseEntity<ApiError> handleFlowManager(
            FlowManagerException exception
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage()
        );
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message
    ) {
        ApiError response = new ApiError(
                Instant.now(),
                status.value(),
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}