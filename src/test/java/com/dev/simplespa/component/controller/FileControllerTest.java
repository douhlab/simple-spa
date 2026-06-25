package com.dev.simplespa.component.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.dev.simplespa.component.controller.dto.DownloadedFileDto;
import com.dev.simplespa.component.controller.dto.FileInfoDto;
import com.dev.simplespa.model.domain.UploadedFile;
import com.dev.simplespa.module.file.AzureBlobService;
import com.dev.simplespa.module.file.LocalFileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AzureBlobService azureBlobService;

	@MockitoBean
	private LocalFileStorageService localFileStorageService;

	@Test
	void uploadToAzure_shouldReturnUploadedFile() throws Exception {
		var file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
		var uploaded = UploadedFile.builder()
			.name("blob-name")
			.originalName("test.txt")
			.uploadedAt(LocalDateTime.now())
			.build();

		when(azureBlobService.uploadFile(any())).thenReturn(uploaded);

		mockMvc.perform(multipart("/simple-spa/v1/api/azure/files/save").file(file))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value("blob-name"))
			.andExpect(jsonPath("originalName").value("test.txt"));
	}

	@Autowired
	private FileController fileController;

	@Test
	void uploadToAzure_shouldRejectLargeFile() {
		var file = new MockMultipartFile("file", "big.bin", "application/octet-stream", new byte[10 * 1024 * 1024 + 1]);

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> fileController.uploadToAzure(file))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("File too large");
	}

	@Test
	void downloadFromAzure_shouldReturnFileContent() throws Exception {
		var downloaded = DownloadedFileDto.builder()
			.content("hello".getBytes())
			.contentType("text/plain")
			.fileName("blob-name")
			.build();

		when(azureBlobService.downloadFile("blob-name")).thenReturn(downloaded);

		mockMvc.perform(get("/simple-spa/v1/api/azure/files/blob-name"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.TEXT_PLAIN))
			.andExpect(content().bytes("hello".getBytes()));
	}

	@Test
	void listAzureFiles_shouldReturnFileList() throws Exception {
		var files = List.of(new FileInfoDto("blob-name", "text/plain", 5, "http://url", Instant.now()));
		when(azureBlobService.listFiles()).thenReturn(files);

		mockMvc.perform(get("/simple-spa/v1/api/azure/files"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].fileName").value("blob-name"));
	}

	@Test
	void deleteFromAzure_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/simple-spa/v1/api/azure/files/blob-name"))
			.andExpect(status().isNoContent());

		verify(azureBlobService).deleteFile("blob-name");
	}

	@Test
	void saveLocally_shouldReturnUploadedFile() throws Exception {
		var file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
		var uploaded = UploadedFile.builder()
			.name("local-file.txt")
			.originalName("test.txt")
			.uploadedAt(LocalDateTime.now())
			.build();

		when(localFileStorageService.save(any())).thenReturn(uploaded);

		mockMvc.perform(multipart("/simple-spa/v1/api/local/files/save").file(file))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value("local-file.txt"));
	}

	@Test
	void saveLocally_shouldRejectLargeFile() {
		var file = new MockMultipartFile("file", "big.bin", "application/octet-stream", new byte[10 * 1024 * 1024 + 1]);

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> fileController.saveLocally(file))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("File too large");
	}

	@Test
	void downloadFromLocal_shouldReturnFile() throws Exception {
		Resource resource = new ByteArrayResource("hello".getBytes()) {
			@Override
			public java.io.File getFile() {
				try {
					var temp = java.io.File.createTempFile("test", ".txt");
					java.nio.file.Files.writeString(temp.toPath(), "hello");
					return temp;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};

		when(localFileStorageService.load("local-file.txt")).thenReturn(resource);

		mockMvc.perform(get("/simple-spa/v1/api/local/files/local-file.txt"))
			.andExpect(status().isOk());
	}

	@Test
	void listLocalFiles_shouldReturnFileList() throws Exception {
		var files = List.of(new FileInfoDto("local-file.txt", "text/plain", 5, "/url", Instant.now()));
		when(localFileStorageService.listFiles()).thenReturn(files);

		mockMvc.perform(get("/simple-spa/v1/api/local/files"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].fileName").value("local-file.txt"));
	}

	@Test
	void deleteFromLocal_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/simple-spa/v1/api/local/files/local-file.txt"))
			.andExpect(status().isNoContent());

		verify(localFileStorageService).deleteFile(eq("local-file.txt"));
	}
}
