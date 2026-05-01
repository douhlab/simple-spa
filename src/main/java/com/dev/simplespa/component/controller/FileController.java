package com.dev.simplespa.component.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.dev.simplespa.component.controller.dto.DownloadedFileDto;
import com.dev.simplespa.component.controller.dto.FileInfoDto;
import com.dev.simplespa.model.domain.UploadedFile;
import com.dev.simplespa.module.file.AzureBlobService;
import com.dev.simplespa.module.file.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${application.product}")
public class FileController {

    private final AzureBlobService azureBlobService;
    private final LocalFileStorageService localFileStorageService;

    @PostMapping("/v1/api/azure/files/save")
    public ResponseEntity<UploadedFile> uploadToAzure(@RequestParam MultipartFile file) throws IOException {
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large");
        }
        var uploadedFile = azureBlobService.uploadFile(file);

        // Save blobName in DB

        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping("/v1/api/azure/files/{blobName}")
    public ResponseEntity<byte[]> downloadFromAzure(@PathVariable String blobName) {

        DownloadedFileDto downloadedFile = azureBlobService.downloadFile(blobName);

        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(downloadedFile.getContentType())) // or dynamic
            .body(downloadedFile.getContent());
    }

    @GetMapping("/v1/api/azure/files")
    public ResponseEntity<List<FileInfoDto>> listFiles() {
        return ResponseEntity.ok(azureBlobService.listFiles());
    }

    @DeleteMapping("/v1/api/azure/files/{fileName}")
    public ResponseEntity<Void> deleteFromAzure(@PathVariable String fileName) {
        azureBlobService.deleteFile(fileName);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/v1/api/local/files/save")
    public ResponseEntity<UploadedFile> saveLocally(@RequestParam MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large");
        }

        var uploadedFile = localFileStorageService.save(file);

        // Save blobName in DB

        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping("/v1/api/local/files/{fileName}")
    public ResponseEntity<Resource> downloadFromLocal(@PathVariable String fileName) throws IOException {

        Resource file = localFileStorageService.load(fileName);
        String contentType = Files.probeContentType(file.getFile().toPath());
        return ResponseEntity.ok()
            .contentType(MediaType.valueOf(contentType)) // or dynamic
            .body(file);
    }

    @GetMapping("/v1/api/local/files")
    public ResponseEntity<List<FileInfoDto>> listLocalFiles() throws IOException {

        return ResponseEntity.ok(localFileStorageService.listFiles());
    }

    @DeleteMapping("/v1/api/local/files/{fileName}")
    public ResponseEntity<Void> deleteFromLocal(@PathVariable String fileName) throws IOException {
        localFileStorageService.deleteFile(fileName);

        return ResponseEntity.noContent().build();
    }
}
