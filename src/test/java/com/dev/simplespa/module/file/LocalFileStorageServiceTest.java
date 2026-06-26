package com.dev.simplespa.module.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.dev.simplespa.component.controller.dto.FileInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

class LocalFileStorageServiceTest {

	private static final int USER_ID = 42;
	private static final int OTHER_USER_ID = 99;

	private LocalFileStorageService service;

	@BeforeEach
	void setUp() {
		service = new LocalFileStorageService();
	}

	@Test
	void save_shouldStoreFileUnderUserFolderAndReturnMetadata() {
		var multipart = new MockMultipartFile("file", "photo.png", "image/png", "image-data".getBytes());

		var result = service.save(USER_ID, multipart);

		assertThat(result.getOriginalName()).isEqualTo("photo.png");
		assertThat(result.getName()).contains("photo.png");
		assertThat(result.getUploadedAt()).isNotNull();
		assertThat(Files.exists(userFilePath(USER_ID, result.getName()))).isTrue();
	}

	@Test
	void load_shouldReturnReadableResourceForUserFile() {
		var multipart = new MockMultipartFile("file", "doc.txt", "text/plain", "hello".getBytes());
		var saved = service.save(USER_ID, multipart);

		Resource resource = service.load(USER_ID, saved.getName());

		assertThat(resource.exists()).isTrue();
		assertThat(resource.isReadable()).isTrue();
	}

	@Test
	void load_shouldThrowWhenFileNotFound() {
		assertThatThrownBy(() -> service.load(USER_ID, "nonexistent-file.txt"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("File not found");
	}

	@Test
	void load_shouldNotAllowAccessToAnotherUsersFile() {
		var multipart = new MockMultipartFile("file", "private.txt", "text/plain", "secret".getBytes());
		var saved = service.save(USER_ID, multipart);

		assertThatThrownBy(() -> service.load(OTHER_USER_ID, saved.getName()))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("File not found");
	}

	@Test
	void listFiles_shouldReturnOnlyCurrentUserFiles() throws IOException {
		var userFile = new MockMultipartFile("file", "listed.txt", "text/plain", "content".getBytes());
		var otherUserFile = new MockMultipartFile("file", "other.txt", "text/plain", "other".getBytes());
		var saved = service.save(USER_ID, userFile);
		var otherSaved = service.save(OTHER_USER_ID, otherUserFile);

		var files = service.listFiles(USER_ID);

		assertThat(files).extracting(FileInfoDto::fileName).contains(saved.getName());
		assertThat(files).extracting(FileInfoDto::fileName).doesNotContain(otherSaved.getName());
	}

	@Test
	void deleteFile_shouldRemoveUserFile() throws IOException {
		var multipart = new MockMultipartFile("file", "to-delete.txt", "text/plain", "content".getBytes());
		var saved = service.save(USER_ID, multipart);
		Path filePath = userFilePath(USER_ID, saved.getName());

		assertThat(Files.exists(filePath)).isTrue();

		service.deleteFile(USER_ID, saved.getName());

		assertThat(Files.exists(filePath)).isFalse();
	}

	private Path userFilePath(int userId, String fileName) {
		return Path.of("local-uploads").resolve(String.valueOf(userId)).resolve(fileName);
	}
}
