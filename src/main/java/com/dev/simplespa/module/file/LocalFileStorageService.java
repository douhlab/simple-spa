package com.dev.simplespa.module.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.dev.simplespa.component.controller.dto.FileInfoDto;
import com.dev.simplespa.model.domain.UploadedFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService {
    private static final String STORAGE_FOLDER_NAME = "local-uploads";

    private final Path rootLocation;

    public LocalFileStorageService() {
        this.rootLocation = Paths.get(STORAGE_FOLDER_NAME).toAbsolutePath().normalize();
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder", e);
        }
    }

    public UploadedFile save(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return UploadedFile.builder()
                .name(filename)
                .originalName(file.getOriginalFilename())
                .uploadedAt(LocalDateTime.now())
                .build(); // Save this in DB
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Resource load(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    public List<FileInfoDto> listFiles() throws IOException {

        Path storagePath = Paths.get(STORAGE_FOLDER_NAME);
        if (!Files.exists(storagePath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> paths = Files.list(storagePath)) {

            return paths
                .filter(Files::isRegularFile)
                .map(this::mapToDto)
                .toList();
        }
    }

    private FileInfoDto mapToDto(Path path) {

        try {
            String fileName = path.getFileName().toString();
            long size = Files.size(path);

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            Instant uploadedAt = Files.getLastModifiedTime(path).toInstant();

            String downloadUrl = "/component/v1/api/local/files/" + fileName;

            return new FileInfoDto(
                fileName,
                contentType,
                size,
                downloadUrl,
                uploadedAt
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String fileName) throws IOException {
        Path storagePath = Paths.get(STORAGE_FOLDER_NAME);

        Path path = storagePath.resolve(fileName);

        Files.deleteIfExists(path);
    }
}
