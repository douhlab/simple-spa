package com.dev.simplespa.module.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

class LocalFileStorageServiceTest {

	private LocalFileStorageService service;

	@BeforeEach
	void setUp() {
		service = new LocalFileStorageService();
	}

	@Test
	void save_shouldStoreFileAndReturnMetadata() {
		var multipart = new MockMultipartFile("file", "photo.png", "image/png", "image-data".getBytes());

		var result = service.save(multipart);

		assertThat(result.getOriginalName()).isEqualTo("photo.png");
		assertThat(result.getName()).contains("photo.png");
		assertThat(result.getUploadedAt()).isNotNull();
		assertThat(Files.exists(Path.of("local-uploads").resolve(result.getName()))).isTrue();
	}

	@Test
	void load_shouldReturnReadableResource() throws IOException {
		var multipart = new MockMultipartFile("file", "doc.txt", "text/plain", "hello".getBytes());
		var saved = service.save(multipart);

		Resource resource = service.load(saved.getName());

		assertThat(resource.exists()).isTrue();
		assertThat(resource.isReadable()).isTrue();
	}

	@Test
	void load_shouldThrowWhenFileNotFound() {
		assertThatThrownBy(() -> service.load("nonexistent-file.txt"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("File not found");
	}

	@Test
	void listFiles_shouldIncludeSavedFile() throws IOException {
		var multipart = new MockMultipartFile("file", "listed.txt", "text/plain", "content".getBytes());
		var saved = service.save(multipart);

		var files = service.listFiles();

		assertThat(files).anyMatch(f -> f.fileName().equals(saved.getName()));
	}

	@Test
	void deleteFile_shouldRemoveFile() throws IOException {
		var multipart = new MockMultipartFile("file", "to-delete.txt", "text/plain", "content".getBytes());
		var saved = service.save(multipart);
		Path filePath = Path.of("local-uploads").resolve(saved.getName());

		assertThat(Files.exists(filePath)).isTrue();

		service.deleteFile(saved.getName());

		assertThat(Files.exists(filePath)).isFalse();
	}
}
