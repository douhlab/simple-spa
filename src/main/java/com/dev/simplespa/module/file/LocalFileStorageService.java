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

	public UploadedFile save(int userId, MultipartFile file) {
		try {
			Path userDirectory = userDirectory(userId);
			Files.createDirectories(userDirectory);

			String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path destination = userDirectory.resolve(filename).normalize();
			validateBelongsToUser(userId, destination);

			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
			return UploadedFile.builder()
				.name(filename)
				.originalName(file.getOriginalFilename())
				.uploadedAt(LocalDateTime.now())
				.build();
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file", e);
		}
	}

	public Resource load(int userId, String filename) {
		try {
			Path file = resolveUserFile(userId, filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() && resource.isReadable()) {
				return resource;
			}
			throw new RuntimeException("File not found");
		} catch (MalformedURLException e) {
			throw new RuntimeException("File not found", e);
		}
	}

	public List<FileInfoDto> listFiles(int userId) throws IOException {
		Path userDirectory = userDirectory(userId);
		if (!Files.exists(userDirectory)) {
			return Collections.emptyList();
		}

		try (Stream<Path> paths = Files.list(userDirectory)) {
			return paths
				.filter(Files::isRegularFile)
				.map(path -> mapToDto(userId, path))
				.toList();
		}
	}

	public void deleteFile(int userId, String fileName) throws IOException {
		Path path = resolveUserFile(userId, fileName);
		Files.deleteIfExists(path);
	}

	private Path userDirectory(int userId) {
		return rootLocation.resolve(String.valueOf(userId)).normalize();
	}

	private Path resolveUserFile(int userId, String filename) {
		Path file = userDirectory(userId).resolve(filename).normalize();
		validateBelongsToUser(userId, file);
		return file;
	}

	private void validateBelongsToUser(int userId, Path file) {
		Path userDirectory = userDirectory(userId).toAbsolutePath().normalize();
		if (!file.toAbsolutePath().normalize().startsWith(userDirectory)) {
			throw new RuntimeException("File not found");
		}
	}

	private FileInfoDto mapToDto(int userId, Path path) {
		try {
			String fileName = path.getFileName().toString();
			long size = Files.size(path);

			String contentType = Files.probeContentType(path);
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			Instant uploadedAt = Files.getLastModifiedTime(path).toInstant();
			String downloadUrl = "/simple-spa/v1/api/local/files/" + fileName;

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
}
