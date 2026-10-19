package com.example.flow_manager.controller;

import com.example.flow_manager.dto.DownloadedFile;
import com.example.flow_manager.dto.FileStatusResponse;
import com.example.flow_manager.dto.FileUploadResponse;
import com.example.flow_manager.service.FileProcessingService;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileProcessingService fileProcessingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file
    ) {
        FileUploadResponse response = fileProcessingService.upload(file);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{fileId}")
    public FileStatusResponse getStatus(@PathVariable UUID fileId) {
        return fileProcessingService.getStatus(fileId);
    }

    @GetMapping("/{fileId}/result")
    public ResponseEntity<ByteArrayResource> downloadResult(
            @PathVariable UUID fileId
    ) {
        DownloadedFile file = fileProcessingService.downloadResult(fileId);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(file.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .contentLength(file.content().length)
                .body(new ByteArrayResource(file.content()));
    }
}