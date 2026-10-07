package com.example.flow_manager.dto;

public record ConvertedFileDto(
        String bucket,
        String objectKey,
        String originalFileName
) {
}