package com.dev.simplespa.module.file;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.dev.simplespa.component.controller.dto.DownloadedFileDto;
import com.dev.simplespa.component.controller.dto.FileInfoDto;
import com.dev.simplespa.model.domain.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AzureBlobService {

	private final BlobContainerClient containerClient;

	public AzureBlobService(
		@Value("${azure.storage.connection-string}") String connectionString,
		@Value("${azure.storage.container-name}") String containerName
	) {
		BlobServiceClient blobServiceClient =
			new BlobServiceClientBuilder()
				.connectionString(connectionString)
				.buildClient();

		this.containerClient = blobServiceClient
			.getBlobContainerClient(containerName);
	}

	public UploadedFile uploadFile(int userId, MultipartFile file) throws IOException {
		String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
		String blobName = userBlobName(userId, fileName);

		BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
		BlobClient blobClient = containerClient.getBlobClient(blobName);

		blobClient.upload(file.getInputStream(), file.getSize(), true);
		blobClient.setHttpHeaders(headers);

		return UploadedFile.builder()
			.name(fileName)
			.originalName(file.getOriginalFilename())
			.uploadedAt(LocalDateTime.now())
			.build();
	}

	public DownloadedFileDto downloadFile(int userId, String fileName) {
		BlobClient blobClient = containerClient.getBlobClient(userBlobName(userId, fileName));

		byte[] content = blobClient.downloadContent().toBytes();
		return DownloadedFileDto.builder()
			.content(content)
			.contentType(blobClient.getProperties().getContentType())
			.fileName(fileName)
			.build();
	}

	public List<FileInfoDto> listFiles(int userId) {
		List<FileInfoDto> files = new ArrayList<>();
		String prefix = userPrefix(userId);
		ListBlobsOptions options = new ListBlobsOptions().setPrefix(prefix);

		for (BlobItem blobItem : containerClient.listBlobs(options, null)) {
			String blobName = blobItem.getName();
			if (!blobName.startsWith(prefix)) {
				continue;
			}

			String fileName = blobName.substring(prefix.length());
			BlobClient blobClient = containerClient.getBlobClient(blobName);
			BlobProperties properties = blobClient.getProperties();

			files.add(new FileInfoDto(
				fileName,
				properties.getContentType(),
				properties.getBlobSize(),
				blobClient.getBlobUrl(),
				properties.getCreationTime().toInstant())
			);
		}

		return files;
	}

	public void deleteFile(int userId, String fileName) {
		BlobClient blobClient = containerClient.getBlobClient(userBlobName(userId, fileName));
		blobClient.delete();
	}

	private String userPrefix(int userId) {
		return userId + "/";
	}

	private String userBlobName(int userId, String fileName) {
		if (fileName.contains("..") || fileName.contains("/")) {
			throw new IllegalArgumentException("Invalid file name");
		}
		return userPrefix(userId) + fileName;
	}
}
