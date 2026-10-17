package com.example.flow_manager.dto;

public record DownloadedFile(
        String fileName,
        byte[] content
) {
}